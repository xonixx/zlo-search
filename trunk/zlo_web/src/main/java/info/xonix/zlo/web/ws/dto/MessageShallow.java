package info.xonix.zlo.web.ws.dto;

import java.util.Date;

/**
 * User: Vovan
 * Date: 19.12.10
 * Time: 23:12
 */
public class MessageShallow {
    protected int id = -1; // default
    protected String nick;
    protected String host;
    protected boolean reg = false;
    protected Date date;
    protected String topic;
    protected String title;

    public MessageShallow(int id, String nick, String host, boolean reg, Date date, String topic, String title) {
        this.id = id;
        this.nick = nick;
        this.host = host;
        this.reg = reg;
        this.date = date;
        this.topic = topic;
        this.title = title;
    }

    public MessageShallow() {
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isReg() {
        return reg;
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

    public void setReg(boolean reg) {
        this.reg = reg;
    }
}
