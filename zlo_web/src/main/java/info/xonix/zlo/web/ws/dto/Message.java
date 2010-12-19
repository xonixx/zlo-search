package info.xonix.zlo.web.ws.dto;

import java.util.Date;

/**
 * User: Vovan
 * Date: 12.12.10
 * Time: 19:09
 */
public class Message extends MessageShallow {
    private String nick;
    private String host;

    private String topic;

    private String title;

    private String body;

    private boolean reg = false;

    private boolean hasUrl;
    private boolean hasImg;

    public Message() {
    }

    public Message(int id, String nick, String host, boolean reg, String topic, String title, String body, Date date, boolean hasUrl, boolean hasImg) {
        this.nick = nick;
        this.host = host;
        this.topic = topic;
        this.title = title;
        this.body = body;
        this.date = date;
        this.reg = reg;
        this.id = id;
        this.hasUrl = hasUrl;
        this.hasImg = hasImg;
    }

    public String getNick() {
        return nick;
    }

    public String getHost() {
        return host;
    }

    public String getTopic() {
        return topic;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isReg() {
        return reg;
    }

    public boolean isHasUrl() {
        return hasUrl;
    }

    public boolean isHasImg() {
        return hasImg;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setReg(boolean reg) {
        this.reg = reg;
    }

    public void setHasUrl(boolean hasUrl) {
        this.hasUrl = hasUrl;
    }

    public void setHasImg(boolean hasImg) {
        this.hasImg = hasImg;
    }
}
