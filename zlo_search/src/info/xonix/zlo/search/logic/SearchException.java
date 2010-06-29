package info.xonix.zlo.search.logic;

/**
* User: Vovan
* Date: 29.06.2010
* Time: 20:57:41
*/
public class SearchException extends RuntimeException {
    private String query;

    public SearchException(String query, Throwable cause) {
        super(cause);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
