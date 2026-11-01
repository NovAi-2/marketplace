package marketplace;

/** Accumulates state: only physical products are shipped, 3.0 per kg. Use one instance per traversal. */
public class ShippingCostVisitor implements Visitor<Void> {
    private static final double RATE_PER_KG = 3.0;
    private double total;

    public double total() { return total; }

    @Override
    public Void visitCatalog(Catalog c) { c.categories().forEach(x -> x.accept(this)); return null; }

    @Override
    public Void visitCategory(Category c) { c.products().forEach(p -> p.accept(this)); return null; }

    @Override
    public Void visitPhysical(PhysicalProduct p) { total += p.weightKg() * RATE_PER_KG; return null; }

    @Override
    public Void visitDigital(DigitalProduct p) { return null; }

    @Override
    public Void visitService(ServiceProduct p) { return null; }

    @Override
    public Void visitSubscription(Subscription p) { return null; }
}
