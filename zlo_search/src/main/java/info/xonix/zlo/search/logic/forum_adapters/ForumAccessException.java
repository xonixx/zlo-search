package info.xonix.zlo.search.logic.forum_adapters;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:34
 */
public class ForumAccessException extends Exception{
    public ForumAccessException(Exception ex) {
        super(ex);
    }

    public ForumAccessException(String msg, Exception ex) {
        super(msg, ex);
    }
}
