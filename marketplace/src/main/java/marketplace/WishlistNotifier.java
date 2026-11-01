package marketplace;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/** Notifies once when a wished product is back in stock, then unsubscribes itself. */
public class WishlistNotifier implements Observer<MarketEvent> {
    private final String productId;
    private final Subject<MarketEvent> stock;
    private final AtomicBoolean notified = new AtomicBoolean();
    private final List<String> messages = new CopyOnWriteArrayList<>();

    public WishlistNotifier(String productId, Subject<MarketEvent> stock) {
        this.productId = productId;
        this.stock = stock;
    }

    @Override
    public void onEvent(MarketEvent e) {
        // compareAndSet: even if several threads get here at once, only one sends the message
        if (e.id().equals(productId) && e.newValue() > 0 && notified.compareAndSet(false, true)) {
            messages.add(productId + " is back in stock: " + (int) e.newValue());
            stock.unsubscribe(EventType.STOCK_CHANGED, this);
        }
    }

    public List<String> messages() {
        return List.copyOf(messages);
    }
}
