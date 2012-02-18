package info.xonix.zlo.search.logic.forum_adapters;

/**
 * User: gubarkov
 * Date: 19.02.12
 * Time: 1:34
 */
public class ForumIoException extends ForumAccessException{
    public ForumIoException(Exception ex) {
        super(ex);
    }

    public ForumIoException(String msg, Exception ex) {
        super(msg, ex);
    }
}
