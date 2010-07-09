package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.site.MessageRetriever;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.utils.Check;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Vovan
 * Date: 12.06.2010
 * Time: 22:00:33
 */
public class SiteLogicImpl implements SiteLogic, InitializingBean {
    private static final Logger log = Logger.getLogger(SiteLogicImpl.class);

//    private PageParser pageParser;
    private MessageRetriever messageRetriever;

    public void setMessageRetriever(MessageRetriever messageRetriever) {
        this.messageRetriever = messageRetriever;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(messageRetriever, "messageRetriever");
    }

    public Message getMessageByNumber(Site site, int num) {
        log.debug(site.getName() + " - Receiving from site: " + num);
        return messageRetriever.getMessage(site, num);
    }

    @Override
    public List<Message> getMessages(Site site, int from, int to) {
        log.info(site.getName() + " - Downloading messages from " + from + " to " + to + "...");
        long begin = System.currentTimeMillis();

        List<Message> msgs = messageRetriever.getMessages(site, from, to);

        float durationSecs = (System.currentTimeMillis() - begin) / 1000f;
        log.info(site.getName() + " - Downloaded " + msgs.size() + " messages in " + (int) durationSecs + "secs. Rate: " + ((float) msgs.size()) / durationSecs + "mps.");

        return msgs;
    }

    @Override
    public int getLastMessageNumber(Site site) {
        return messageRetriever.getLastMessageNumber(site);
    }


/*    private PageRetriever retreiver;

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
    }*/

/*    public int getLastRootMessageNumber() throws IOException {
        return getRetriever().getLastRootMessageNumber();
    }*/

    private List<Site> sites;

    @Override
    public List<Site> getSites() {
        if (sites == null) {
            sites = new LinkedList<Site>();

            for (Object key : Config.getAppProperties().keySet()) {
                String k = (String) key;
                if (k.startsWith(Config.SITE_CONFIG_PREFIX)) {
                    sites.add(Site.forName(k.replaceFirst(Config.SITE_CONFIG_PREFIX, "")));
                }
            }
            Collections.sort(sites, new Comparator<Site>() {
                public int compare(Site o1, Site o2) {
                    return new Integer(o1.getSiteNumber()).compareTo(o2.getSiteNumber());
                }
            });
        }
        return sites;
    }

    @Override
    public String[] getSiteNames() {
        List<Site> allSites = getSites();
        String[] sites = new String[allSites.size()];
        for (int i = 0; i < allSites.size(); i++) {
            sites[i] = allSites.get(i).getSiteUrl();
        }
        return sites;
    }

    @Override
    public Site getSite(int num) {
        for (Site site : getSites()) {
            if (site.getSiteNumber() == num) {
                return site;
            }
        }
        return null;
    }

/*    private ZloSearcher zloSearcher;

    public ZloSearcher getZloSearcher() {
        if (zloSearcher == null) {
            if (getName() != null) {
                zloSearcher = Site.forName(getName()).getZloSearcher();
            } else
                zloSearcher = new ZloSearcher(this);
        }
        return zloSearcher;
    }*/
}
