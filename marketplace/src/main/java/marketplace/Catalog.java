package marketplace;

import java.util.List;

/** Root container (level 1). */
public record Catalog(List<Category> categories) implements Element {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCatalog(this);
    }
}
