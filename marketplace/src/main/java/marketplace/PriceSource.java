package marketplace;

/** Subject 1: price changes. */
public class PriceSource extends EventPublisher {
    public void change(Product product, double newPrice) {
        double oldPrice = product.replacePrice(newPrice);
        publish(new MarketEvent(EventType.PRICE_CHANGED, product.id(), oldPrice, newPrice));
    }
}
