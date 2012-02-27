package info.xonix.zlo.search.config.forums;

import info.xonix.zlo.search.logic.forum_adapters.ForumAdapter;

import java.util.List;

/**
 * User: gubarkov
 * Date: 27.02.12
 * Time: 23:02
 */
public class Forums {
    private List<ForumAdapter> forums;

    public Forums(List<ForumAdapter> forums) {
        this.forums = forums;
    }
}
