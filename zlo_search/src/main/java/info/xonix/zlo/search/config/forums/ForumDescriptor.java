package info.xonix.zlo.search.config.forums;

import info.xonix.zlo.search.logic.forum_adapters.ForumAdapter;

/**
 * User: gubarkov
 * Date: 29.02.12
 * Time: 2:13
 */
public class ForumDescriptor {
    private String forumId;
    private ForumParams forumParams;
    private ForumAdapter forumAdapter;

    public ForumDescriptor(
            String forumId,
            ForumParams forumParams,
            ForumAdapter forumAdapter) {

        this.forumId = forumId;
        this.forumParams = forumParams;
        this.forumAdapter = forumAdapter;
    }

    public String getForumId() {
        return forumId;
    }

    public ForumParams getForumParams() {
        return forumParams;
    }

    public ForumAdapter getForumAdapter() {
        return forumAdapter;
    }
}
