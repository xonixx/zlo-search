package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.domainobj.Site;
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
    List<Site> getSites();

    String[] getSiteNames();

    @Nullable
    Site getSite(int num);

    int getLastMessageNumber(Site site) throws RetrieverException;

    List<Message> getMessages(Site site, int from, int to) throws RetrieverException, PageParseException;

    Message getMessageByNumber(Site site, int num) throws RetrieverException, PageParseException;
}
