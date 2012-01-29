package info.xonix.zlo.search.model;

/**
 * User: Vovan
 * Date: 29.06.2010
 * Time: 21:55:58
 */
public interface MessageFields {
    String TITLE = "title"; // clean - w/o html
    String TOPIC_CODE = "topicCode";
    String URL_NUM = "num";
    String NICK = "nick";
    String REG = "reg";

    String HOST = "host";
    String HOST_REVERSED = "host_r";

    String DATE = "date";
    String BODY = "body"; // clean - w/o html
    String HAS_URL = "url";
    String HAS_IMG = "img";
}
