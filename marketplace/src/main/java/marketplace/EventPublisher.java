package marketplace;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Thread-safe listener registry: one copy-on-write list per event type, no global lock. */
public class EventPublisher implements Subject<MarketEvent> {
    private final Map<EventType, List<Observer<MarketEvent>>> observers = new ConcurrentHashMap<>();

    @Override
    public void subscribe(EventType type, Observer<MarketEvent> observer) {
        observers.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>()).add(observer);
    }

    @Override
    public void unsubscribe(EventType type, Observer<MarketEvent> observer) {
        List<Observer<MarketEvent>> list = observers.get(type);
        if (list != null) {
            list.remove(observer);
        }
    }

    /** Iterates over a snapshot, so listeners may (un)subscribe while an event is being delivered. */
    protected void publish(MarketEvent event) {
        for (Observer<MarketEvent> observer : observers.getOrDefault(event.type(), List.of())) {
            observer.onEvent(event);
        }
    }
}
