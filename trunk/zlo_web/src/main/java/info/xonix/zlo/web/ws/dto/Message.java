package info.xonix.zlo.web.ws.dto;

import java.util.Date;

/**
 * User: Vovan
 * Date: 12.12.10
 * Time: 19:09
 */
public class Message extends MessageShallow {

    private String body;

    private boolean hasUrl;
    private boolean hasImg;

    public Message() {
        super();
    }

    public Message(int id, String nick, String host, boolean reg, String topic, String title, String body, Date date, boolean hasUrl, boolean hasImg) {
        super(id, nick, host, reg, date, topic, title);
        this.body = body;
        this.hasUrl = hasUrl;
        this.hasImg = hasImg;
    }

    public String getBody() {
        return body;
    }

    public boolean isHasUrl() {
        return hasUrl;
    }

    public boolean isHasImg() {
        return hasImg;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHasUrl(boolean hasUrl) {
        this.hasUrl = hasUrl;
    }

    public void setHasImg(boolean hasImg) {
        this.hasImg = hasImg;
    }
}
