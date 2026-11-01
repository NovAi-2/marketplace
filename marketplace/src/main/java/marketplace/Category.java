package marketplace;

import java.util.List;

/** Second container (level 2). */
public record Category(String name, List<Product> products) implements Element {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCategory(this);
    }
}
