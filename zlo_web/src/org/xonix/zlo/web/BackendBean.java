package org.xonix.zlo.web;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.model.ZloMessage;

/**
 * Author: Vovan
 * Date: 03.09.2007
 * Time: 20:03:46
 */
public class BackendBean {
    private int topic;
    private String title;
    private String body;
    private String nick;
    private String host;
    private String fromDate;
    private String toDate;
    private int site;
    private int pageSize;

    public static final String SN_TOPIC = "topic";
    public static final String SN_SITE = "site";
    public static final String SN_PAGE_SIZE = "pageSize";

    public BackendBean() {
    }

    public String getTopicSelector() {
        String[] topics = new String[0];
        try {
            // todo: check
            topics = DbManager.forSite(Site.getSite(getSite())).getTopics();
        } catch (DbException e) {
            ;
        }
        return HtmlConstructor.constructSelector(SN_TOPIC, null, new String[]{ZloMessage.ALL_TOPICS}, topics, topic, true);
    }

    public String getSiteSelector() {
        return HtmlConstructor.constructSelector(SN_SITE, null, Site.getSiteNames(), site, true);
    }

    public String getPageSizeSelector() {
        return HtmlConstructor.constructSelector(SN_PAGE_SIZE, null, Config.NUMS_PER_PAGE, pageSize, true);
    }

    public int getSite() {
        return site;
    }

    public void setSite(int site) {
        this.site = site;
    }

    public int getTopic() {
        return topic;
    }

    public void setTopic(int topic) {
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

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
