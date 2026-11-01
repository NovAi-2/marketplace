package marketplace;

public interface Observer<E> {
    void onEvent(E event);
}
