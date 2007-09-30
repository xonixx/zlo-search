package org.xonix.zlo.search;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * Author: Vovan
 * Date: 30.09.2007
 * Time: 3:08:16
 */
public class SearchRequest {

    private String text;
    private String nick;
    private String host;
    private String topicCode;

    private Date fromDate;
    private Date toDate;

    public SearchRequest() {
    }

    public SearchRequest(String text, String nick, String host, String topicCode, Date fromDate, Date toDate) {
        this.text = text;
        this.nick = nick;
        this.host = host;
        this.topicCode = topicCode;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        return StringUtils.isNotEmpty(text) ||
                StringUtils.isNotEmpty(nick) ||
                StringUtils.isNotEmpty(host) ||
                StringUtils.isNotEmpty(topicCode) && !"0".equals(topicCode);
    }

    public boolean isTheSameSearch(String topicCode,
                                     String text,
                                     String nick,
                                     String host,
                                     Date fromDate,
                                     Date toDate) {
        return StringUtils.equals(this.topicCode, topicCode) &&
                StringUtils.equals(this.text, text) &&
                StringUtils.equals(this.nick, nick) &&
                StringUtils.equals(this.host, host) &&
                (this.fromDate == fromDate || this.fromDate != null && this.fromDate.equals(fromDate)) &&
                (this.toDate == toDate || this.toDate != null && this.toDate.equals(toDate));
    }

    public boolean isNotTheSameSearch(String topicCode,
                                     String text,
                                     String nick,
                                     String host,
                                     Date fromDate,
                                     Date toDate) {
        return !isTheSameSearch(topicCode, text, nick, host, fromDate, toDate);
    }

    public ZloSearchResult performSearch() {
        ZloSearchResult result = ZloSearcher.search(this);

        result.setTopicCode(topicCode);
        result.setText(text);
        result.setNick(nick);
        result.setHost(host);
        result.setFromDate(fromDate);
        result.setToDate(toDate);

        return result;
    }
}
