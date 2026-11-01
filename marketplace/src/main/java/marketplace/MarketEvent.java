package marketplace;

/** Immutable event (push model): carries everything a listener needs. */
public record MarketEvent(EventType type, String id, double oldValue, double newValue) { }
