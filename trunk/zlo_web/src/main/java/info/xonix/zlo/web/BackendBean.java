package info.xonix.zlo.web;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.MessagesDao;

import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * Author: Vovan
 * Date: 03.09.2007
 * Time: 20:03:46
 */
public class BackendBean {
    private final static Logger log = Logger.getLogger(BackendBean.class);

    public static final String ALL_TOPICS = "Все темы";

    private String topic;
    private String title;
    private String body;
    private String nick;
    private String host;
    private String fromDate;
    private String toDate;
    private String site;
    private String pageSize;

    public static final String SN_TOPIC = "topic";
    public static final String SN_SITE = "site";
    public static final String SN_PAGE_SIZE = "pageSize";
    public static final int SITE_URL_MAX_LEN = 30;

    private static final Config config = AppSpringContext.get(Config.class);
    private static final SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);
    private static final MessagesDao MESSAGES_DAO = AppSpringContext.get(MessagesDao.class);

    public BackendBean() {
    }

    public String getTopicSelector() {
        String[] topics = new String[0];

        // todo: check
        String forumId1 = siteLogic.getSite(getSiteInt());
        try {
            topics = MESSAGES_DAO.getTopics(site1);
        } catch (DataAccessException e) {
            log.warn("Can't get topics, as db exception occurred: " + e);
        }

        return HtmlConstructor.constructSelector(SN_TOPIC, null,
                new String[]{ALL_TOPICS}, topics, getTopicInt(), true);
    }

    public String getSiteSelector() {
        return HtmlConstructor.constructSelector(SN_SITE, SN_SITE, null,
                formSiteNums(siteLogic.getSites()),
                formSiteHosts(siteLogic.getSiteNames()), getSiteInt(), true);
    }

    private String[] formSiteNums(List<Site> sites) {
        String[] siteNums = new String[sites.size()];
        int i = 0;
        for (String forumId1 : sites) {
            siteNums[i++] = String.valueOf(site1.getSiteNumber());
        }
        return siteNums;
    }


    private String[] formSiteHosts(String[] siteNames) {
        String[] res = new String[siteNames.length];

        int i = 0;
        for (String siteName : siteNames) {
//            res[i++] = StringUtils.substringBefore(siteName, "/");
            res[i++] = siteName.length() <= SITE_URL_MAX_LEN
                    ? siteName
                    : StringUtils.substring(siteName, 0, SITE_URL_MAX_LEN - 3) + "...";
        }
        return res;
    }

    public String getPageSizeSelector() {
        return HtmlConstructor.constructSelector(SN_PAGE_SIZE, null, config.getNumsPerPage(), getPageSizeInt(), true);
    }

    public String getSite() {
        return site;
    }

    public int getSiteInt() {
        return NumberUtils.toInt(forumId, 0);
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTopic() {
        return topic;
    }

    public int getTopicInt() {
        return NumberUtils.toInt(topic, -1);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getPageSize() {
        return pageSize;
    }

    public int getPageSizeInt() {
        return NumberUtils.toInt(pageSize, 0);
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
}
