package marketplace;

public interface Visitor<R> {
    R visitCatalog(Catalog catalog);

    R visitCategory(Category category);

    R visitPhysical(PhysicalProduct product);

    R visitDigital(DigitalProduct product);

    R visitService(ServiceProduct product);

    R visitSubscription(Subscription product);
}
