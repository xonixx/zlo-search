package org.xonix.zlo.search;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * Author: Vovan
 * Date: 30.09.2007
 * Time: 3:08:16
 */
public class SearchRequest {
    private String title;
    private String body;
    private String nick;
    private String host;
    private String topicCode;

    private Date fromDate;
    private Date toDate;

    public SearchRequest() {
    }

    public SearchRequest(String title, String body, String nick, String host, String topicCode, Date fromDate, Date toDate) {
        this.title = title;
        this.body = body;
        this.nick = nick;
        this.host = host;
        this.topicCode = topicCode;
        this.fromDate = fromDate;
        this.toDate = toDate;
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

    public String getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(String topicCode) {
        this.topicCode = topicCode;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean canBeProcessed() {
        return StringUtils.isNotEmpty(title) ||
                StringUtils.isNotEmpty(body) ||
                StringUtils.isNotEmpty(nick) ||
                StringUtils.isNotEmpty(host) ||
                StringUtils.isNotEmpty(topicCode) && !"0".equals(topicCode);
    }

    public boolean isTheSameSearch(String topicCode,
                                     String title,
                                     String body,
                                     String nick,
                                     String host,
                                     Date fromDate,
                                     Date toDate) {
        return StringUtils.equals(this.topicCode, topicCode) &&
                StringUtils.equals(this.title, title) &&
                StringUtils.equals(this.body, body) &&
                StringUtils.equals(this.nick, nick) &&
                StringUtils.equals(this.host, host) &&
                (this.fromDate == fromDate || this.fromDate != null && this.fromDate.equals(fromDate)) &&
                (this.toDate == toDate || this.toDate != null && this.toDate.equals(toDate));
    }

    public boolean isNotTheSameSearch(String topicCode,
                                     String title,
                                     String body,
                                     String nick,
                                     String host,
                                     Date fromDate,
                                     Date toDate) {
        return !isTheSameSearch(topicCode, title, body, nick, host, fromDate, toDate);
    }

    public ZloSearchResult performSearch() {
        return ZloSearcher.search(this);
    }
}
