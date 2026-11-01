package marketplace;

public class DigitalProduct extends Product {
    public DigitalProduct(String id, String name, double price) {
        super(id, name, price);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitDigital(this);
    }
}
