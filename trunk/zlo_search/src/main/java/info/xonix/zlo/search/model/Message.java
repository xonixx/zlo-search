package info.xonix.zlo.search.model;

import info.xonix.zlo.search.FoundTextHighlighter;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.utils.HtmlUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Date;

import static org.apache.lucene.document.Field.Index;
import static org.apache.lucene.document.Field.Store;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 20:18:10
 */
public class Message implements Serializable, ZloMessageAccessor {
    private static final String TRUE = "1";
    private static final String FALSE = "0";

/*    public static final String TOPIC = "topic";
    public static final String TITLE_HTML = "titleHtml";
    public static final String BODY_HTML = "bodyHtml";
    public static final String STATUS = "status";*/

    private String nick;
    private String altName;
    private String host;
    private String topic;
    private int topicCode;
    private String title;
    private String body;
    private Date date;
    private boolean reg = false;
    private int num = -1; // default
    private int parentNum = -1; // default

    private String titleClean;
    private String bodyClean;
    private Boolean hasUrl = null;
    private Boolean hasImg = null;

    private int hitId;

    private Site site;

    public static Comparator<Message> NUM_COMPARATOR = new Comparator<Message>() {
        public int compare(Message m1, Message m2) {
            return new Integer(m1.getNum()).compareTo(m2.getNum());
        }
    };

    private MessageStatus status = MessageStatus.UNKNOWN; // default

    public static String formQueryString(String text, boolean inTitle, boolean inBody, int topicCode, String nick, String host, Date fromDate, Date toDate, boolean inReg, boolean inHasUrl, boolean inHasImg) {
        text = FoundTextHighlighter.escapeColon(text);

        StringBuilder queryStr = new StringBuilder();

        nick = StringUtils.lowerCase(nick);
        host = StringUtils.lowerCase(host);

        if (StringUtils.isNotEmpty(text)) {
            if (inTitle && !inBody)
                queryStr.append(MessageFormat.format(" +{0}:({1})", MessageFields.TITLE, text));

            else if (!inTitle && inBody)
                queryStr.append(MessageFormat.format(" +{0}:({1})", MessageFields.BODY, text));

            else if (inTitle && inBody)
                queryStr.append(MessageFormat.format(" +({0}:({2}) OR {1}:({2}))", MessageFields.TITLE, MessageFields.BODY, text));

            else // !inTitle && !inBody
                queryStr.append(MessageFormat.format(" +{0}:({2}) +{1}:({2})", MessageFields.TITLE, MessageFields.BODY, text));
        }

        if (-1 != topicCode) {
            queryStr.append(MessageFormat.format(" +{0}:{1}", MessageFields.TOPIC_CODE, topicCode));
        }

        if (StringUtils.isNotEmpty(nick))
            queryStr.append(MessageFormat.format(" +{0}:(\"{1}\")", MessageFields.NICK, nick));

        if (StringUtils.isNotEmpty(host))
            queryStr.append(MessageFormat.format(" +{0}:({1})", MessageFields.HOST, host));

        if (fromDate != null && toDate != null)
            queryStr.append(MessageFormat.format(" +{0}:[{1,date,yyyyMMdd} TO {2,date,yyyyMMdd}]", MessageFields.DATE, fromDate, toDate));

        if (inReg)
            queryStr.append(MessageFormat.format(" +{0}:1", MessageFields.REG));

        if (inHasUrl)
            queryStr.append(MessageFormat.format(" +{0}:1", MessageFields.HAS_URL));

        if (inHasImg)
            queryStr.append(MessageFormat.format(" +{0}:1", MessageFields.HAS_IMG));

        return queryStr.toString();
    }

    public static final String ALL_TOPICS = "Все темы";

    public static NumberFormat URL_NUM_FORMAT = new DecimalFormat("0000000000"); // 10 zeros

    public Message() {
    }

    public Message(Site site, String nick, String altName, String host, String topic, int topicCode,
                   String title, String body, Date msgDate,
                   boolean reg, int num, int parentNum,
                   Boolean hasUrl, Boolean hasImg,
                   MessageStatus status) {
        this.site = site;
        this.nick = nick;
        this.altName = altName;
        this.host = host;
        this.topic = topic;
        this.topicCode = topicCode;
        this.title = title;
        this.body = body;
        this.date = msgDate;
        this.reg = reg;
        this.num = num;
        this.parentNum = parentNum;
        this.hasUrl = hasUrl;
        this.hasImg = hasImg;
        this.status = status;
    }

    public Message(Site site, String nick, String altName, String host, String topic, int topicCode,
                   String title, String body, Date msgDate,
                   boolean reg, int num, int parentNum,
                   int status) {
        this(site,
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
                null,
                null,
                MessageStatus.fromInt(status));
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
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

    public int getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(int topicCode) {
        this.topicCode = topicCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Timestamp getTimestamp() {
        return getDate() == null
                ? null
                : new Timestamp(getDate().getTime());
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

    public boolean isHasUrl() {
        if (hasUrl == null) {
            hasUrl = StringUtils.isNotEmpty(body) && HtmlUtils.hasUrl(body);
        }
        return hasUrl;
    }

    public boolean isHasImg() {
        if (hasImg == null) {
            hasImg = StringUtils.isNotEmpty(body) && HtmlUtils.hasImg(body, site);
        }
        return hasImg;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Message getMessage() {
        return this;
    }

    public String toString() {
        if (isOk())
            return MessageFormat.format(
                    "Message(" +
                            "\n\tnum={0},\n\tparentNum={1},\n\ttopicCode={2},\n\ttopic={3}," +
                            "\n\ttitle={4},\n\tnick={5},\n\taltName={6},\n\treg={7}," +
                            "\n\thost={8},\n\tdate={9,date,MMMM, d HH:mm:ss yyyy},\n\thasUrl={10},\n\thasImg={11}," +
                            "\n\tsite={12}," +
                            "\n\tbody={13}\n)",
                    num, parentNum, topicCode, topic, title,
                    nick, altName, reg, host, date,
                    isHasUrl() ? TRUE : FALSE,
                    isHasImg() ? TRUE : FALSE,
                    site == null ? "" : site.getName(),
                    body.replaceAll("\n", "\n\t\t"));
        else
            return MessageFormat.format(
                    "Message(" +
                            "\n\tnum={0},\n\tstatus={1}," +
                            "\n\tsite={2}" +
                            "\n)",
                    num, status,
                    site == null ? "" : site.getName());
    }

    public boolean isOk() {
        return status == MessageStatus.OK;
    }

    public Document getDocument() {
        if (!isOk()) // index only OK messages
            return null;

        Document doc = new Document();

        doc.add(new Field(MessageFields.URL_NUM, URL_NUM_FORMAT.format(num), Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.TOPIC_CODE, Integer.toString(topicCode), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.TITLE, getCleanTitle(), Store.NO, Index.ANALYZED)); // "чистый" - индексируем, не храним
        doc.add(new Field(MessageFields.NICK, nick.toLowerCase(), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.REG, reg ? TRUE : FALSE, Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.HOST, host.toLowerCase(), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.DATE, DateTools.dateToString(date, DateTools.Resolution.MINUTE), Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.BODY, getCleanBody(), Store.NO, Index.ANALYZED)); // "чистый" - индексируем, не храним
        doc.add(new Field(MessageFields.HAS_URL, isHasUrl() ? TRUE : FALSE, Store.NO, Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.HAS_IMG, isHasImg() ? TRUE : FALSE, Store.NO, Index.NOT_ANALYZED));

        return doc;
    }

    private static final long serialVersionUID = -3231624250115810539L; // for deserialization
}
