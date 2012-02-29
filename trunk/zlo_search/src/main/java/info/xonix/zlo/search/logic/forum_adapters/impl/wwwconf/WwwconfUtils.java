package info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf;

import info.xonix.zlo.search.config.forums.GetForum;

/**
 * User: gubarkov
 * Date: 29.02.12
 * Time: 19:38
 */
public class WwwconfUtils {
    public static WwwconfParams getWwwconfParams(String forumId) {
        return ((WwwconfForumAdapter) GetForum.adapter(forumId)).getWwwconfParams();
    }
}
