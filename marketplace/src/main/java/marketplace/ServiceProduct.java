package marketplace;

public class ServiceProduct extends Product {
    public ServiceProduct(String id, String name, double price) {
        super(id, name, price);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitService(this);
    }
}
