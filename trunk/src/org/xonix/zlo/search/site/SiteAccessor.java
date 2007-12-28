package org.xonix.zlo.search.site;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 28.12.2007
 * Time: 2:45:21
 */
public abstract class SiteAccessor {

    public static final String SITE_ACCESSOR_PREFIX = "site.accessor.";

    public String END_MSG_MARK;
    public String END_MSG_MARK_SIGN;
    public String MSG_NOT_EXIST_OR_WRONG;
    public String WITHOUT_TOPIC;

    // regexes
    public String MSG_REG_RE_STR;
    public String MSG_UNREG_RE_STR;
    public String INDEX_UNREG_RE_STR;

    public String INDEXING_URL;
    public String READ_QUERY;


    public static SiteAccessor forSite(String siteName) {
        try {
            return (SiteAccessor) Class.forName(Config.getProp(SITE_ACCESSOR_PREFIX + siteName)).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ZloMessage getMessage(int num) throws IOException {
        return getParser().parseMessage(getRetriever().getPageContentByNumber(num), num);
    }

    private PageRetriever retreiver;
    private PageRetriever getRetriever() {
        if (retreiver == null) {
            retreiver = new PageRetriever(this);
        }
        return retreiver;
    }

    private PageParser parser;
    private PageParser getParser() {
        if (parser == null) {
            parser = new PageParser(this);
        }
        return parser;
    }

    public int getLastRootMessageNumber() throws IOException {
        return getRetriever().getLastRootMessageNumber(); 
    }
}
