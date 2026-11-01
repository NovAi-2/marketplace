package marketplace;

/** Base of all leaves (level 3). Data only; the price is volatile because feeds change it. */
public abstract class Product implements Element {
    private final String id;
    private final String name;
    private volatile double price;

    protected Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String id() { return id; }

    public String name() { return name; }

    public double price() { return price; }

    /** Sets the new price and returns the old one in one atomic step. */
    public synchronized double replacePrice(double newPrice) {
        double old = price;
        price = newPrice;
        return old;
    }
}
