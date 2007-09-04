package org.xonix.zlo.web;

import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.Config;

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

    public BackendBean() {
    }

    public String getTopicSelector() {
        StringBuilder res = new StringBuilder("<select name=\"topic\">\n");

        for (int i=0; i<ZloMessage.TOPICS.length; i++) {
            if (i == topic)
                res.append("<option value=\"")
                        .append(i).append("\" selected>")
                        .append(ZloMessage.TOPICS[i]).append("</option>\n");
            else
                res.append("<option value=\"")
                        .append(i).append("\">")
                        .append(ZloMessage.TOPICS[i]).append("</option>\n");
        }

        res.append("</select>");
        return res.toString();
    }

    public String getSiteSelector() {
        StringBuilder res = new StringBuilder("<select name=\"site\">\n");
        for (int i=0; i<Config.SITES.length; i++){
            if (i == site)
                res.append("<option value=\"")
                        .append(i).append("\" selected>")
                        .append(Config.SITES[i]).append("</option>\n");
            else
                res.append("<option value=\"")
                        .append(i).append("\">")
                        .append(Config.SITES[i]).append("</option>\n");
        }
        res.append("</select>");
        return res.toString();
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
}
