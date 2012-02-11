package info.xonix.zlo.search.xmlfp;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 0:31
 */
public class XmlFpUrls {
    private String lastMessageNumberUrl;
    private String messageUrl;

    public XmlFpUrls(String lastMessageNumberUrl, String messageUrl) {
        this.lastMessageNumberUrl = lastMessageNumberUrl;
        this.messageUrl = messageUrl;
    }

    public String getLastMessageNumberUrl() {
        return lastMessageNumberUrl;
    }

    public String getMessageUrl() {
        return messageUrl;
    }
}
