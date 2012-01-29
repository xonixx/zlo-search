package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.site.MessageRetriever;
import info.xonix.zlo.search.logic.site.PageParseException;
import info.xonix.zlo.search.logic.site.RetrieverException;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.utils.Check;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
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

    private Config config;
    private MessageRetriever messageRetriever;

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setMessageRetriever(MessageRetriever messageRetriever) {
        this.messageRetriever = messageRetriever;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(messageRetriever, "messageRetriever");
        Check.isSet(config, "config");
    }

    @Override
    public Message getMessageByNumber(Site site, int num) throws RetrieverException, PageParseException {
        log.debug(site.getName() + " - Receiving from site: " + num);
        return messageRetriever.getMessage(site, num);
    }

    @Override
    public List<Message> getMessages(Site site, int from, int to) throws RetrieverException, PageParseException {
        log.info(site.getName() + " - Downloading messages from " + from + " to " + to + "...");
        long begin = System.currentTimeMillis();

        List<Message> msgs = messageRetriever.getMessages(site, from, to);

        float durationSecs = (System.currentTimeMillis() - begin) / 1000f;
        log.info(site.getName() + " - Downloaded " + msgs.size() + " messages in " + (int) durationSecs + "secs. Rate: " + ((float) msgs.size()) / durationSecs + "mps.");

        return msgs;
    }

    @Override
    public int getLastMessageNumber(Site site) throws RetrieverException {
        return messageRetriever.getLastMessageNumber(site);
    }

    private List<Site> sites;

    @Override
    public List<Site> getSites() {
        if (sites == null) {
            sites = new LinkedList<Site>();

            final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            final Resource[] resources;
            try {
                resources = resolver.getResources(Config.FORUMS_CONF_PATH + "**/*.properties");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (Resource siteFileResource : resources) {
                sites.add(Site.forName(FilenameUtils.removeExtension(siteFileResource.getFilename())));
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
}
