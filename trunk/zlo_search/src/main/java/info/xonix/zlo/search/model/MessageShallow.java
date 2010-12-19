package info.xonix.zlo.search.model;

import java.util.Date;

/**
 * User: Vovan
 * Date: 20.12.10
 * Time: 1:09
 */
public class MessageShallow {
    protected int num = -1; // default
    protected String nick;
    protected String host;
    protected boolean reg = false;
    protected String topic;
    protected String title;
    protected Date date;

    public MessageShallow() {
    }

    public MessageShallow(int num, String nick, String host, boolean reg, String topic, String title, Date date) {
        this.num = num;
        this.nick = nick;
        this.host = host;
        this.reg = reg;
        this.topic = topic;
        this.title = title;
        this.date = date;
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

    public String getTopic() {
        return topic;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isReg() {
        return reg;
    }

    public void setReg(boolean reg) {
        this.reg = reg;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
