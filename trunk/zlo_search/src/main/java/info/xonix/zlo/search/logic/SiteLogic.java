package info.xonix.zlo.search.logic;


import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.logic.site.PageParseException;
import info.xonix.zlo.search.logic.site.RetrieverException;
import info.xonix.zlo.search.model.Message;

import javax.annotation.Nullable;
import java.util.List;

/**
 * User: Vovan
 * Date: 12.06.2010
 * Time: 22:00:19
 */
public interface SiteLogic {
//    List<Site> getSites();

    String[] getSiteNames();

/*    @Nullable
    Site getSite(int num);*/

    int getLastMessageNumber(String forumId) throws RetrieverException;

    List<Message> getMessages(String forumId, int from, int to) throws ForumAccessException;

    Message getMessageByNumber(String forumId, int num) throws ForumAccessException;
}
