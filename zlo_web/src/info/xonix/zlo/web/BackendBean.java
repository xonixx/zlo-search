package info.xonix.zlo.web;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.model.ZloMessage;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Author: Vovan
 * Date: 03.09.2007
 * Time: 20:03:46
 */
public class BackendBean {
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

    public BackendBean() {
    }

    public String getTopicSelector() {
        String[] topics = new String[0];
        try {
            // todo: check
            topics = Site.getSite(getSiteInt()).getDbManager().getTopics();
        } catch (DbException e) {
            ;
        }
        return HtmlConstructor.constructSelector(SN_TOPIC, null, new String[]{ZloMessage.ALL_TOPICS}, topics, getTopicInt(), true);
    }

    public String getSiteSelector() {
        return HtmlConstructor.constructSelector(SN_SITE, null, formSiteHosts(Site.getSiteNames()), getSiteInt(), true);
    }

    private String[] formSiteHosts(String[] siteNames) {
        String[] res = new String[siteNames.length];

        int i=0;
        for (String siteName : siteNames){
//            res[i++] = StringUtils.substringBefore(siteName, "/");
            res[i++] = siteName.length() <= SITE_URL_MAX_LEN
                    ? siteName
                    : StringUtils.substring(siteName, 0, SITE_URL_MAX_LEN - 3) + "...";
        }
        return res;
    }

    public String getPageSizeSelector() {
        return HtmlConstructor.constructSelector(SN_PAGE_SIZE, null, Config.NUMS_PER_PAGE, getPageSizeInt(), true);
    }

    public String getSite() {
        return site;
    }

    public int getSiteInt() {
        return NumberUtils.toInt(site, 0);
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
