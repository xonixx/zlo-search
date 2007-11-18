package org.xonix.zlo.web;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.web.servlets.SearchServlet;

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

    public static final String SN_TOPIC = SearchServlet.QS_TOPIC;
    public static final String SN_SITE = SearchServlet.QS_SITE;
    public static final String SN_PAGE_SIZE = SearchServlet.QS_PAGE_SIZE;

    public BackendBean() {
    }

    public String getTopicSelector() {
        return HtmlConstructor.constructSelector(SN_TOPIC, ZloMessage.ALL_TOPICS, DbManager.getTopics(), topic, true);
    }

    public String getSiteSelector() {
        return HtmlConstructor.constructSelector(SN_SITE, Config.SITES, site, true);
    }

    public String getPageSizeSelector() {
        return HtmlConstructor.constructSelector(SN_PAGE_SIZE, Config.NUMS_PER_PAGE, pageSize, true);
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
