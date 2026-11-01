package marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class ConcurrencyTest {
    @Test
    @Timeout(30)
    void stressTest4ThreadsTimes1000EventsGivesExactCount() throws InterruptedException {
        Marketplace m = new Marketplace(4);
        m.run(1000);
        assertEquals(4000, m.analytics.total());
        assertEquals(40, m.log.count(EventType.STATS_UPDATED)); // one report per 100 events
    }

    @Test
    @Timeout(30)
    void wishlistIsNotifiedExactlyOnceWhenThreadsPublishAtTheSameTime() throws InterruptedException {
        Marketplace m = new Marketplace(4);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (int t = 0; t < 4; t++) {
            pool.submit(() -> {
                for (int i = 0; i < 1000; i++) {
                    m.stock.change("laptop", 0, 5);
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(20, TimeUnit.SECONDS));
        assertEquals(1, m.wishlist.messages().size());
    }
}
