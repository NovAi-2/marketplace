package marketplace;

import java.util.List;
import java.util.function.Predicate;

/** Filter: returns all products that match a condition. */
public class FilterVisitor implements Visitor<List<Product>> {
    private final Predicate<Product> condition;

    public FilterVisitor(Predicate<Product> condition) {
        this.condition = condition;
    }

    private List<Product> check(Product p) { return condition.test(p) ? List.of(p) : List.of(); }

    @Override
    public List<Product> visitCatalog(Catalog c) { return c.categories().stream().flatMap(x -> x.accept(this).stream()).toList(); }

    @Override
    public List<Product> visitCategory(Category c) { return c.products().stream().flatMap(p -> p.accept(this).stream()).toList(); }

    @Override
    public List<Product> visitPhysical(PhysicalProduct p) { return check(p); }

    @Override
    public List<Product> visitDigital(DigitalProduct p) { return check(p); }

    @Override
    public List<Product> visitService(ServiceProduct p) { return check(p); }

    @Override
    public List<Product> visitSubscription(Subscription p) { return check(p); }
}
