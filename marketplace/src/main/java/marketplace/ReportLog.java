package marketplace;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Final listener of the chain: stores statistics and category reports. */
public class ReportLog implements Observer<MarketEvent> {
    private final Queue<MarketEvent> entries = new ConcurrentLinkedQueue<>();

    @Override
    public void onEvent(MarketEvent e) {
        entries.add(e);
    }

    public long count(EventType type) {
        return entries.stream().filter(e -> e.type() == type).count();
    }

    public List<MarketEvent> entries() {
        return List.copyOf(entries);
    }
}
