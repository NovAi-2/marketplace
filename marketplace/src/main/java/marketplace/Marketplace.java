package marketplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/** Composition root: the only place that creates objects and wires them together. */
public class Marketplace {
    final Catalog catalog;
    final List<Product> products = new ArrayList<>();
    final PriceSource prices = new PriceSource();
    final StockSource stock = new StockSource();
    final WishlistNotifier wishlist = new WishlistNotifier("laptop", stock);
    final PriceCache cache = new PriceCache();
    final Analytics analytics = new Analytics();
    final ReportLog log = new ReportLog();
    final PriceDropAlert alert;
    private final int threads;

    public Marketplace(int threads) {
        this.threads = threads;
        this.catalog = new Catalog(List.of(
                new Category("Electronics", List.of(
                        new PhysicalProduct("laptop", "Laptop", 1000, 2.0),
                        new PhysicalProduct("phone", "Phone", 500, 0.5))),
                new Category("Digital", List.of(
                        new DigitalProduct("game", "Game", 60),
                        new Subscription("music", "Music plan", 10))),
                new Category("Services", List.of(
                        new ServiceProduct("repair", "Repair", 80)))));

        Map<String, Category> categoryOf = new HashMap<>();
        for (Category c : catalog.categories()) {
            for (Product p : c.products()) {
                categoryOf.put(p.id(), c);
                products.add(p);
            }
        }
        this.alert = new PriceDropAlert(Map.copyOf(categoryOf));

        prices.subscribe(EventType.PRICE_CHANGED, cache);
        prices.subscribe(EventType.PRICE_CHANGED, analytics);
        prices.subscribe(EventType.PRICE_CHANGED, alert);
        stock.subscribe(EventType.STOCK_CHANGED, cache);
        stock.subscribe(EventType.STOCK_CHANGED, analytics);
        stock.subscribe(EventType.STOCK_CHANGED, wishlist);
        alert.subscribe(EventType.CATEGORY_REPORT, log);
        analytics.subscribe(EventType.STATS_UPDATED, log);
    }

    /** Starts `threads` producers, each publishing `eventsPerProducer` events, and waits for all of them. */
    public void run(int eventsPerProducer) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> produce(eventsPerProducer));
        }
        pool.shutdown(); // no new tasks, queued ones still finish
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
            pool.shutdownNow();
            throw new IllegalStateException("producers did not finish in time");
        }
    }

    private void produce(int events) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < events; i++) {
            Product p = products.get(random.nextInt(products.size()));
            if (i % 2 == 0) {
                prices.change(p, 10 + random.nextInt(1000));
            } else {
                stock.change(p.id(), 5, random.nextInt(10));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Marketplace m = new Marketplace(4);
        m.run(1000);
        System.out.println("Events processed: " + m.analytics.total());
        System.out.println("Stats updates:    " + m.log.count(EventType.STATS_UPDATED));
        System.out.println("Category reports: " + m.log.count(EventType.CATEGORY_REPORT));
        System.out.println("Wishlist:         " + m.wishlist.messages());
        System.out.println(m.catalog.accept(new XmlExportVisitor()));
    }
}
