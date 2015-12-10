package info.xonix.zlo.search;

import info.xonix.zlo.search.logic.SearchLogicImpl;
import org.apache.lucene.search.Sort;

/**
 * User: gubarkov
 * Date: 17.04.12
 * Time: 22:37
 */
public enum SortBy {
    DATE("date", SearchLogicImpl.REVERSED_INDEX_ORDER_SORT),
    RELEVANCE("rel", Sort.RELEVANCE);

    private final String name;

    private final Sort sort;

    SortBy(String name, Sort sort) {
        this.name = name;
        this.sort = sort;
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

    public Sort getSort() {
        return sort;
    }
}
