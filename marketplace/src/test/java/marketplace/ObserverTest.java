package marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ObserverTest {
    private final Marketplace m = new Marketplace(4);

    private Product find(String id) {
        return m.products.stream().filter(p -> p.id().equals(id)).findFirst().orElseThrow();
    }

    @Test
    void listenerReceivesOnlyItsEventType() {
        EventPublisher publisher = new EventPublisher();
        List<MarketEvent> got = new ArrayList<>();
        publisher.subscribe(EventType.PRICE_CHANGED, got::add);
        publisher.publish(new MarketEvent(EventType.STOCK_CHANGED, "laptop", 1, 2));
        publisher.publish(new MarketEvent(EventType.PRICE_CHANGED, "laptop", 1, 2));
        assertEquals(1, got.size());
    }

    @Test
    void unsubscribedListenerGetsNothing() {
        EventPublisher publisher = new EventPublisher();
        List<MarketEvent> got = new ArrayList<>();
        Observer<MarketEvent> listener = got::add;
        publisher.subscribe(EventType.PRICE_CHANGED, listener);
        publisher.unsubscribe(EventType.PRICE_CHANGED, listener);
        publisher.publish(new MarketEvent(EventType.PRICE_CHANGED, "laptop", 1, 2));
        assertEquals(0, got.size());
    }

    @Test
    void wishlistNotifiesOnceAndUnsubscribesItselfInsideHandler() {
        m.stock.change("laptop", 0, 5);
        m.stock.change("laptop", 5, 8);
        assertEquals(1, m.wishlist.messages().size());
    }

    @Test
    void wishlistIgnoresOutOfStockAndOtherProducts() {
        m.stock.change("laptop", 5, 0);
        m.stock.change("phone", 0, 5);
        assertEquals(0, m.wishlist.messages().size());
    }

    @Test
    void cacheKeepsLatestPrice() {
        m.prices.change(find("phone"), 450);
        assertEquals(450.0, m.cache.get("phone"), 1e-9);
    }

    @Test
    void cacheDropsOutOfStockProduct() {
        m.prices.change(find("phone"), 450);
        m.stock.change("phone", 5, 0);
        assertNull(m.cache.get("phone"));
    }

    @Test
    void analyticsCountsEventsFromBothSources() {
        m.prices.change(find("phone"), 450);
        m.stock.change("phone", 5, 3);
        assertEquals(2, m.analytics.total());
    }

    @Test
    void analyticsRepublishesTotalEvery100Events() {
        for (int i = 0; i < 250; i++) {
            m.stock.change("phone", 1, 2);
        }
        assertEquals(250, m.analytics.total());
        assertEquals(2, m.log.count(EventType.STATS_UPDATED));
    }

    @Test
    void priceDropReportsTaxOfItsCategoryOnly() {
        m.prices.change(find("laptop"), 800); // Electronics tax: 800*0.12 + 500*0.12 = 156
        MarketEvent report = m.log.entries().get(0);
        assertEquals(EventType.CATEGORY_REPORT, report.type());
        assertEquals("Electronics", report.id());
        assertEquals(156.0, report.newValue(), 1e-9);
    }

    @Test
    void priceDropInAnotherCategoryReportsThatCategory() {
        m.prices.change(find("game"), 40); // Digital tax: 40*0.10 + 10*0.10 = 5
        MarketEvent report = m.log.entries().get(0);
        assertEquals("Digital", report.id());
        assertEquals(5.0, report.newValue(), 1e-9);
    }

    @Test
    void priceIncreaseDoesNotCreateReport() {
        m.prices.change(find("laptop"), 1200);
        assertEquals(0, m.log.count(EventType.CATEGORY_REPORT));
    }
}
