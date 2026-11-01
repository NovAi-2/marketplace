package marketplace;

public class Subscription extends Product {
    public Subscription(String id, String name, double price) {
        super(id, name, price);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitSubscription(this);
    }
}
