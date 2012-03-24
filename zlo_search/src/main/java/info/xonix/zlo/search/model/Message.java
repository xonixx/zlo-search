package info.xonix.zlo.search.model;

import info.xonix.zlo.search.utils.HtmlUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 20:18:10
 */
public class Message extends MessageShallow implements Serializable {
    public static final int NO_PARENT = -1;

    private String altName;
    private int topicCode;
    private String body;
    private int parentNum = NO_PARENT; // default

    private String titleClean;
    private String bodyClean;

    private int hitId; // TODO: this should not be here

    public static Comparator<Message> NUM_COMPARATOR = new Comparator<Message>() {
        public int compare(Message m1, Message m2) {
            return new Integer(m1.getNum()).compareTo(m2.getNum());
        }
    };

    private MessageStatus status = null; //MessageStatus.UNKNOWN; // default

    public Message() {
        super();
    }

    public Message(String nick, String altName, String host, String topic, int topicCode,
                   String title, String body, Date msgDate,
                   boolean reg, int num, int parentNum,
                   MessageStatus status) {

        super(num, nick, host, reg, topic, title, msgDate);

        this.altName = altName;
        this.topicCode = topicCode;
        this.body = body;
        this.parentNum = parentNum;
        this.status = status;
    }

    public Message(String nick, String altName, String host, String topic, int topicCode,
                   String title, String body, Date msgDate,
                   boolean reg, int num, int parentNum,
                   int status) {
        this(
                nick,
                altName,
                host,
                topic,
                topicCode,
                title,
                body,
                msgDate,
                reg,
                num,
                parentNum,
                MessageStatus.fromInt(status));
    }

    public static Message withStatus(MessageStatus status, int id) {
        final Message message = new Message();
        message.setNum(id);
        message.setStatus(status);
        return message;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public int getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(int topicCode) {
        this.topicCode = topicCode;
    }

    public String getCleanTitle() {
        if (titleClean == null)
            titleClean = HtmlUtils.cleanHtml(title);
        return titleClean;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCleanBody() {
        if (bodyClean == null)
            bodyClean = HtmlUtils.cleanBoardSpecific(body);
        return bodyClean;
    }

    public Timestamp getTimestamp() {
        return getDate() == null
                ? null
                : new Timestamp(getDate().getTime());
    }

    public int getParentNum() {
        return parentNum;
    }

    public void setParentNum(int parentNum) {
        this.parentNum = parentNum;
    }

    public int getHitId() {
        return hitId;
    }

    public void setHitId(int hitId) {
        this.hitId = hitId;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Message getMessage() {
        return this;
    }

    public boolean isOk() {
        return status == MessageStatus.OK;
    }

    public String toString() {
        if (isOk()) {
            return MessageFormat.format(
                    "Message(" +
                            "\n\tnum={0},\n\tparentNum={1},\n\ttopicCode={2},\n\ttopic={3}," +
                            "\n\ttitle={4},\n\tnick={5},\n\taltName={6},\n\treg={7}," +
                            "\n\thost={8},\n\tdate={9,date,MMMM, d HH:mm:ss yyyy}," +
                            "\n\tbody={10}\n)",
                    num, parentNum, topicCode, topic, title,
                    nick, altName, reg, host, date,
                    body != null ? body.replaceAll("\n", "\n\t\t") : "");
        } else {
            return MessageFormat.format(
                    "Message(" +
                            "\n\tnum={0},\n\tstatus={1}," +
                            "\n)",
                    num, status);
        }
    }

    private static final long serialVersionUID = -3231624250115810539L; // for deserialization
}
