package marketplace;

import java.util.stream.Collectors;

/** Hand-written XML export; nesting follows the tree. */
public class XmlExportVisitor implements Visitor<String> {
    private String leaf(String tag, Product p) {
        return "<" + tag + " id=\"" + p.id() + "\" price=\"" + p.price() + "\"/>";
    }

    @Override
    public String visitCatalog(Catalog c) {
        return "<catalog>" + c.categories().stream().map(x -> x.accept(this)).collect(Collectors.joining()) + "</catalog>";
    }

    @Override
    public String visitCategory(Category c) {
        return "<category name=\"" + c.name() + "\">"
                + c.products().stream().map(p -> p.accept(this)).collect(Collectors.joining()) + "</category>";
    }

    @Override
    public String visitPhysical(PhysicalProduct p) { return leaf("physical", p); }

    @Override
    public String visitDigital(DigitalProduct p) { return leaf("digital", p); }

    @Override
    public String visitService(ServiceProduct p) { return leaf("service", p); }

    @Override
    public String visitSubscription(Subscription p) { return leaf("subscription", p); }
}
