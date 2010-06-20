package info.xonix.zlo.search.model;

import info.xonix.zlo.search.IndexingSource;
import info.xonix.zlo.search.MultithreadedRetriever;
import info.xonix.zlo.search.ZloSearcher;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.site.PageParser;
import info.xonix.zlo.search.site.PageRetriever;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Author: Vovan
* Date: 11.01.2008
* Time: 22:30:46
*/
public class Site extends SiteConfiguration implements IndexingSource {

    private static Logger logger = Logger.getLogger("Site");

    private Site(String siteName) {
        super(siteName);
    }

    private static HashMap<String, Site> allSites = new HashMap<String, Site>();
    public static Site forName(String siteName) {
        if (!allSites.containsKey(siteName)) {
            allSites.put(siteName, new Site(siteName));
        }
        return allSites.get(siteName);
    }

    public ZloMessage getMessageByNumber(int num) throws DAOException {
        logger.debug(getName() + " - Receiving from site: " + num);
        try {
            return getMessage(num);
        } catch (IOException e) {
            throw new DAOException(this, e);
        } catch (RuntimeException e) {
            logger.error("Exception while saving msg #" + num);
            throw e;
        }
    }

    public List<ZloMessage> getMessages(int from, int to) throws DAOException {
        logger.info(getName() + " - Downloading messages from " + from + " to " + to + "...");
        long begin = System.currentTimeMillis();

        List<ZloMessage> msgs = MultithreadedRetriever.getMessages(this, from, to);

        float durationSecs = (System.currentTimeMillis() - begin) / 1000f;
        logger.info(getName() + " - Downloaded " + msgs.size() + " messages in " + (int)durationSecs + "secs. Rate: " + ((float)msgs.size()) / durationSecs + "mps.");

        return msgs;
    }

    public int getLastMessageNumber() throws DAOException {
        try {
            return getLastRootMessageNumber();
        } catch (IOException e) {
            throw new DAOException(this, e);
        }
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

    private static List<Site> sites;
    public static List<Site> getSites() {
        if (sites == null) {
            sites = new ArrayList<Site>();

            for (Object key : Config.getAppProperties().keySet()) {
                String k = (String) key;
                if (k.startsWith(SITE_CONFIG_PREFIX)) {
                    sites.add(Site.forName(k.replaceFirst(SITE_CONFIG_PREFIX, "")));
                }
            }
            Collections.sort(sites, new Comparator<Site>(){
                public int compare(Site o1, Site o2) {
                    return new Integer(o1.getSiteNumber()).compareTo(o2.getSiteNumber());
                }
            });
        }
        return sites;
    }

    public static String[] getSiteNames() {
        List<Site> allSites = getSites();
        String[] sites = new String[allSites.size()];
        for (int i=0; i<allSites.size(); i++) {
            sites[i] = allSites.get(i).getSiteUrl();
        }
        return sites;
    }

    public static Site getSite(int num) {
        for (Site site : getSites()) {
            if (site.getNum() == num) {
                return site;
            }
        }
        return null;
    }

    public Integer getNum() {
        return getSiteNumber();
    }

    private ZloSearcher zloSearcher;
    public ZloSearcher getZloSearcher() {
        if (zloSearcher == null) {
            if (getName() != null) {
                zloSearcher = Site.forName(getName()).getZloSearcher();
            } else
                zloSearcher = new ZloSearcher(this);
        }
        return zloSearcher;
    }

    public String toString() {
        return "Site(" + getName() + ")";
    }
}
