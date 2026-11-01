package marketplace;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Keeps the latest price of every product; out-of-stock products are removed. */
public class PriceCache implements Observer<MarketEvent> {
    private final Map<String, Double> prices = new ConcurrentHashMap<>();

    @Override
    public void onEvent(MarketEvent e) {
        if (e.type() == EventType.PRICE_CHANGED) {
            prices.put(e.id(), e.newValue());
        } else if (e.newValue() == 0) {
            prices.remove(e.id());
        }
    }

    public Double get(String productId) {
        return prices.get(productId);
    }
}
