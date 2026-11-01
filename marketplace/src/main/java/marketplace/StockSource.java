package marketplace;

/** Subject 2: stock changes. */
public class StockSource extends EventPublisher {
    public void change(String productId, int oldQty, int newQty) {
        publish(new MarketEvent(EventType.STOCK_CHANGED, productId, oldQty, newQty));
    }
}
