package info.xonix.zlo.search.config.forums;

import info.xonix.zlo.search.logic.forum_adapters.ForumAdapter;

/**
 * User: gubarkov
 * Date: 29.02.12
 * Time: 2:13
 */
public class ForumDescriptor {
    private final int forumIntId;
    private final String forumId;

    private final ForumParams forumParams;
    private final ForumAdapter forumAdapter;
    private boolean dead;

    public ForumDescriptor(
            int forumIntId,
            String forumId,
            ForumParams forumParams,
            ForumAdapter forumAdapter) {

        this.forumIntId = forumIntId;
        this.forumId = forumId;

        this.forumParams = forumParams;
        this.forumAdapter = forumAdapter;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

    public String getForumId() {
        return forumId;
    }

    public int getForumIntId() {
        return forumIntId;
    }

    public ForumParams getForumParams() {
        return forumParams;
    }

    public ForumAdapter getForumAdapter() {
        return forumAdapter;
    }
}
