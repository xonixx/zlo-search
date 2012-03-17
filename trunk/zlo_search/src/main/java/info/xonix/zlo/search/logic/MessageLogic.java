package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.logic.forum_adapters.ForumAdapter;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.utils.HtmlUtils;

/**
 * User: gubarkov
 * Date: 17.03.12
 * Time: 1:34
 */
public class MessageLogic {
    public static boolean hasUrl(Message message) {
        return HtmlUtils.hasUrl(message.getBody());
    }

    public static boolean hasImg(Message message, String forumId) {
        final ForumAdapter adapter = GetForum.adapter(forumId);
        return HtmlUtils.hasImg(message.getBody(), adapter.getForumHost());
    }
}
