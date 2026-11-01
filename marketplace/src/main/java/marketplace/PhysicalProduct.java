package marketplace;

public class PhysicalProduct extends Product {
    private final double weightKg;

    public PhysicalProduct(String id, String name, double price, double weightKg) {
        super(id, name, price);
        this.weightKg = weightKg;
    }

    public double weightKg() { return weightKg; }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitPhysical(this);
    }
}
