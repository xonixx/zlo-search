package info.xonix.zlo.search.domain;

/**
 * User: gubarkov
 * Date: 17.04.12
 * Time: 22:37
 */
public enum SortBy {
    DATE("date"),
    RELEVANCE("rel");

    private String name;

    SortBy(String name) {
        this.name = name;
    }

    public static SortBy byName(String name) {
        for (SortBy value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return DATE; // default sort by date
    }

    public String getName() {
        return name;
    }
}
