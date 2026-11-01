package marketplace;

public interface Subject<E> {
    void subscribe(EventType type, Observer<E> observer);

    void unsubscribe(EventType type, Observer<E> observer);
}
