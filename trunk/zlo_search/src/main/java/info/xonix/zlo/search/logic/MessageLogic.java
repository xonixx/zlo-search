package info.xonix.zlo.search.logic;

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
        return HtmlUtils.hasImg(message.getBody(), "TBD"); // TODO: !!! we should pass site http://hostname here

    }
}
