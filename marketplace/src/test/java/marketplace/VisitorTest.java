package marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class VisitorTest {
    private final Catalog catalog = new Marketplace(4).catalog;

    @Test
    void taxOfWholeCatalog() {
        // Electronics 120+60, Digital 6+1, Services 4
        assertEquals(191.0, catalog.accept(new TaxVisitor()), 1e-9);
    }

    @Test
    void taxOfOneCategory() {
        assertEquals(180.0, catalog.categories().get(0).accept(new TaxVisitor()), 1e-9);
    }

    @Test
    void eachLeafTypeHasItsOwnTaxRate() { // proof of double dispatch
        assertEquals(12.0, new PhysicalProduct("a", "A", 100, 1).accept(new TaxVisitor()), 1e-9);
        assertEquals(10.0, new DigitalProduct("b", "B", 100).accept(new TaxVisitor()), 1e-9);
        assertEquals(5.0, new ServiceProduct("c", "C", 100).accept(new TaxVisitor()), 1e-9);
        assertEquals(10.0, new Subscription("d", "D", 100).accept(new TaxVisitor()), 1e-9);
    }

    @Test
    void shippingAccumulatesOverWholeTree() {
        ShippingCostVisitor shipping = new ShippingCostVisitor();
        catalog.accept(shipping);
        assertEquals(7.5, shipping.total(), 1e-9); // (2.0 + 0.5) kg * 3.0
    }

    @Test
    void filterReturnsMatchingProductsInTreeOrder() {
        List<Product> expensive = catalog.accept(new FilterVisitor(p -> p.price() > 70));
        assertEquals(List.of("laptop", "phone", "repair"), expensive.stream().map(Product::id).toList());
    }

    @Test
    void filterReturnsEmptyListWhenNothingMatches() {
        assertTrue(catalog.accept(new FilterVisitor(p -> p.price() > 5000)).isEmpty());
    }

    @Test
    void xmlOfSmallTree() {
        Catalog small = new Catalog(List.of(new Category("A", List.of(new PhysicalProduct("p1", "Pen", 10.0, 0.1)))));
        assertEquals("<catalog><category name=\"A\"><physical id=\"p1\" price=\"10.0\"/></category></catalog>",
                small.accept(new XmlExportVisitor()));
    }

    @Test
    void xmlOfFullCatalogIsNestedCorrectly() {
        String xml = catalog.accept(new XmlExportVisitor());
        assertTrue(xml.startsWith("<catalog><category name=\"Electronics\"><physical id=\"laptop\""));
        assertTrue(xml.endsWith("</category></catalog>"));
        assertEquals(3, xml.split("<category ", -1).length - 1);
    }
}
