package marketplace;

/** Numeric calculation: total tax of a subtree. */
public class TaxVisitor implements Visitor<Double> {
    @Override
    public Double visitCatalog(Catalog c) { return c.categories().stream().mapToDouble(x -> x.accept(this)).sum(); }

    @Override
    public Double visitCategory(Category c) { return c.products().stream().mapToDouble(p -> p.accept(this)).sum(); }

    @Override
    public Double visitPhysical(PhysicalProduct p) { return p.price() * 0.12; }

    @Override
    public Double visitDigital(DigitalProduct p) { return p.price() * 0.10; }

    @Override
    public Double visitService(ServiceProduct p) { return p.price() * 0.05; }

    @Override
    public Double visitSubscription(Subscription p) { return p.price() * 0.10; }
}
