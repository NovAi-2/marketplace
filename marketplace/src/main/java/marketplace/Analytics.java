package marketplace;

import java.util.concurrent.atomic.AtomicLong;

/** Listener AND subject (observer chain): counts events and republishes the total every 100 events. */
public class Analytics extends EventPublisher implements Observer<MarketEvent> {
    private static final int BATCH = 100;
    private final AtomicLong total = new AtomicLong();

    @Override
    public void onEvent(MarketEvent e) {
        long n = total.incrementAndGet();
        if (n % BATCH == 0) {
            publish(new MarketEvent(EventType.STATS_UPDATED, "all", n - BATCH, n));
        }
    }

    public long total() {
        return total.get();
    }
}
