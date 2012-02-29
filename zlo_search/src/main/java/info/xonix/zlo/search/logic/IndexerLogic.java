package info.xonix.zlo.search.logic;

/**
 * User: Vovan
 * Date: 21.06.2010
 * Time: 0:16:10
 */
public interface IndexerLogic {
    void index(String forumId, int from, int to) throws IndexerException;

    void closeIndexWriter(String forumId);

    void closeIndexWriters();
}
