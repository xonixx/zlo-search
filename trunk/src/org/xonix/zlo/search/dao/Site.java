package org.xonix.zlo.search.dao;

import org.xonix.zlo.search.IndexingSource;
import org.xonix.zlo.search.MultithreadedRetriever;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.site.SiteAccessor;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: Vovan
* Date: 11.01.2008
* Time: 22:30:46
*/
public class Site extends SiteAccessor implements IndexingSource {

    public Site(String siteName) {
        super(siteName);
    }

    public ZloMessage getMessageByNumber(int num) throws DAOException {
        DAO.logger.debug("Receiving from site: " + num);
        try {
            return getMessage(num);
        } catch (IOException e) {
            throw new DAOException(this, e);
        }
    }

    public List<ZloMessage> getMessages(int from, int to) throws DAOException {
        DAO.logger.info("Downloading messages from " + from + " to " + to + "...");
        long begin = System.currentTimeMillis();

        List<ZloMessage> msgs = MultithreadedRetriever.getMessages(this, from, to);

        float durationSecs = (System.currentTimeMillis() - begin) / 1000f;
        DAO.logger.info("Downloaded " + msgs.size() + " messages in " + (int)durationSecs + "secs. Rate: " + ((float)msgs.size()) / durationSecs + "mps.");

        return msgs;
    }

    public int getLastMessageNumber() throws DAOException {
        try {
            return getLastRootMessageNumber();
        } catch (IOException e) {
            throw new DAOException(this, e);
        }
    }

    private static List<Site> sites;
    public static List<Site> getSites() {
        if (sites == null) {
            sites = new ArrayList<Site>();

            for (Object key : Config.getAppProperties().keySet()) {
                String k = (String) key;
                if (k.startsWith(SITE_CONFIG_PREFIX)) {
                    sites.add(new Site(k.replaceFirst(SITE_CONFIG_PREFIX, "")));
                }
            }
        }
        return sites;
    }

    public static String[] getSiteNames() {
        List<Site> allSites = getSites();
        String[] sites = new String[allSites.size()];
        for (int i=0; i<allSites.size(); i++) {
            sites[i] = allSites.get(i).SITE_URL;
        }
        return sites;
    }

    public static Site getSite(int num) {
        return getSites().get(num);
    }
}
