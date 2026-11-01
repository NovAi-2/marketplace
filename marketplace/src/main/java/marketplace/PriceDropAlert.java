package marketplace;

import java.util.Map;

/** Integration: price drop -> TaxVisitor on that product's category only -> CATEGORY_REPORT event. */
public class PriceDropAlert extends EventPublisher implements Observer<MarketEvent> {
    private final Map<String, Category> categoryOf;

    public PriceDropAlert(Map<String, Category> categoryOf) {
        this.categoryOf = categoryOf;
    }

    @Override
    public void onEvent(MarketEvent e) {
        if (e.newValue() >= e.oldValue()) {
            return;
        }
        Category category = categoryOf.get(e.id());
        double tax = category.accept(new TaxVisitor());
        publish(new MarketEvent(EventType.CATEGORY_REPORT, category.name(), 0, tax));
    }
}
