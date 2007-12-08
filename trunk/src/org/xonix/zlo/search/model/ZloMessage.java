package org.xonix.zlo.search.model;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hit;
import org.apache.commons.lang.StringUtils;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.ZloSearcher;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.utils.HtmlUtils;

import java.io.IOException;
import java.io.Serializable;
import java.text.*;
import java.util.Comparator;
import java.util.Date;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 20:18:10
 */
public class ZloMessage implements Serializable, ZloMessageAccessor {
    private static final String TRUE = "1";
    private static final String FALSE = "0";

    public static final String TOPIC = "topic";
    public static final String TITLE_HTML = "titleHtml";
    public static final String BODY_HTML = "bodyHtml";
    public static final String STATUS = "status";

    public static final class FIELDS {
        public static final String TITLE = "title"; // clean - w/o html
        public static final String TOPIC_CODE = "topicCode";
        public static final String URL_NUM = "num";
        public static final String NICK = "nick";
        public static final String REG = "reg";
        public static final String HOST = "host";
        public static final String DATE = "date";
        public static final String BODY = "body"; // clean - w/o html
        public static final String HAS_URL = "url";
        public static final String HAS_IMG = "img";
    }

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

    public static Comparator<ZloMessage> NUM_COMPARATOR = new Comparator<ZloMessage>() {
            public int compare(ZloMessage m1, ZloMessage m2) {
                return m1.getNum() > m2.getNum() ? 1 : m1.getNum() < m2.getNum() ? -1 : 0;
            }
        };

    private Status status = Status.UNKNOWN; // default

    public static enum Status {
        OK,
        DELETED,
        SPAM,
        UNKNOWN,
        ;

        public static Status fromInt(int id) {
            return Status.values()[id];
        }

        public int getInt() {
            for(int i=0; i<Status.values().length; i++) {
                if (this == Status.values()[i])
                    return i;
            }
            return -1;
        }
    }

    public static String formQueryString(String text, boolean inTitle, boolean inBody, int topicCode, String _nick, String _host, Date fromDate, Date toDate, boolean inReg, boolean inHasUrl, boolean inHasImg) {
        StringBuilder queryStr = new StringBuilder();

        String nick = _nick.toLowerCase();
        String host = _host.toLowerCase();

        if (StringUtils.isNotEmpty(text)) {
            if (inTitle && !inBody)
                queryStr.append(MessageFormat.format(" +{0}:({1})", FIELDS.TITLE, text));

            else if (!inTitle && inBody)
                queryStr.append(MessageFormat.format(" +{0}:({1})", FIELDS.BODY, text));

            else if (inTitle && inBody)
                queryStr.append(MessageFormat.format(" +({0}:({2}) OR {1}:({2}))", FIELDS.TITLE, FIELDS.BODY, text));

            else // !inTitle && !inBody
                queryStr.append(MessageFormat.format(" +{0}:({2}) +{1}:({2})", FIELDS.TITLE, FIELDS.BODY, text));
        }

        if (-1 != topicCode) {
            queryStr.append(MessageFormat.format(" +{0}:{1}", FIELDS.TOPIC_CODE, topicCode));
        }

        if (StringUtils.isNotEmpty(nick))
            queryStr.append(MessageFormat.format(" +{0}:\"{1}\"", FIELDS.NICK, nick));

        if (StringUtils.isNotEmpty(host))
            queryStr.append(MessageFormat.format(" +{0}:{1}", FIELDS.HOST, host));

        if (fromDate != null && toDate != null)
            queryStr.append(MessageFormat.format(" +{0}:[{1,date,yyyyMMdd} TO {2,date,yyyyMMdd}]", FIELDS.DATE, fromDate, toDate));

        if (inReg)
            queryStr.append(MessageFormat.format(" +{0}:1", FIELDS.REG));

        if (inHasUrl)
            queryStr.append(MessageFormat.format(" +{0}:1", FIELDS.HAS_URL));

        if (inHasImg)
            queryStr.append(MessageFormat.format(" +{0}:1", FIELDS.HAS_IMG));

        return queryStr.toString();
    }

    public static final String ALL_TOPICS = "Все темы";

    public static NumberFormat URL_NUM_FORMAT = new DecimalFormat("0000000000"); // 10 zeros

    public ZloMessage() {
    }

    public ZloMessage(String nick, String altName, String host, String topic, int topicCode,
                      String title, String body, Date msgDate,
                      boolean reg, int num, int parentNum,
                      Boolean hasUrl, Boolean hasImg,
                      Status status) {
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

    public ZloMessage(String nick, String altName, String host, String topic, int topicCode,
                      String title, String body, Date msgDate,
                      boolean reg, int num, int parentNum,
                      int status) {
        this(nick,
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
            Status.fromInt(status));
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
        if (hasUrl == null)
            hasUrl = HtmlUtils.hasUrl(body);
        return hasUrl;
    }

    public boolean isHasImg() {
        if (hasImg == null)
            hasImg = HtmlUtils.hasImg(body);
        return hasImg;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ZloMessage getMessage() {
        return this;
    }

    public String toString() {
        if (status == Status.OK)
            return MessageFormat.format(
                    "ZloMessage(" +
                    "\n\tnum={0},\n\tparentNum={1},\n\ttopicCode={2},\n\ttopic={3}," +
                    "\n\ttitle={4},\n\tnick={5},\n\taltName={6},\n\treg={7}," +
                    "\n\thost={8},\n\tdate={9,date,MMMM, d HH:mm:ss yyyy},\n\thasUrl={10},\n\thasImg={11}," +
                    "\n\tbody={12}\n)",
                    num, parentNum, topicCode, topic, title,
                    nick, altName, reg, host, date,
                    isHasUrl() ? TRUE : FALSE,
                    isHasImg() ? TRUE : FALSE,
                    body.replaceAll("\n","\n\t\t"));
        else
            return MessageFormat.format(
                    "ZloMessage(" +
                    "\n\tnum={0},\n\tstatus={1}\n)",
                    num, status);
    }

    public Document getDocument() {
        if (status != Status.OK) // index only OK messages
            return null;

        Document doc = new Document();

        doc.add(new Field(FIELDS.URL_NUM, URL_NUM_FORMAT.format(num), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(FIELDS.TOPIC_CODE, Integer.toString(topicCode), Field.Store.NO, Field.Index.UN_TOKENIZED));
        doc.add(new Field(FIELDS.TITLE, getCleanTitle(), Field.Store.NO, Field.Index.TOKENIZED)); // "чистый" - индексируем, не храним
        doc.add(new Field(FIELDS.NICK, nick.toLowerCase(), Field.Store.NO, Field.Index.UN_TOKENIZED));
        doc.add(new Field(FIELDS.REG, reg ? TRUE : FALSE, Field.Store.NO, Field.Index.UN_TOKENIZED));
        doc.add(new Field(FIELDS.HOST, host.toLowerCase(), Field.Store.NO, Field.Index.UN_TOKENIZED));
        doc.add(new Field(FIELDS.DATE, DateTools.dateToString(date, DateTools.Resolution.MINUTE), Field.Store.NO, Field.Index.UN_TOKENIZED));
        doc.add(new Field(FIELDS.BODY, getCleanBody(), Field.Store.NO, Field.Index.TOKENIZED)); // "чистый" - индексируем, не храним
        doc.add(new Field(FIELDS.HAS_URL, isHasUrl() ? TRUE : FALSE, Field.Store.NO, Field.Index.UN_TOKENIZED));
        doc.add(new Field(FIELDS.HAS_IMG, isHasImg() ? TRUE : FALSE, Field.Store.NO, Field.Index.UN_TOKENIZED));

        return doc;
    }

    public static ZloMessage fromDocument(Document doc) {
        int num = Integer.parseInt(doc.get(FIELDS.URL_NUM));

        ZloMessage msg;
        try {
            msg = DAO.DB._getMessageByNumber(num);
        } catch (DAO.DAOException e) {
            throw new RuntimeException(e);
        }
        return msg;
    }

    public static ZloMessage fromDocument(Document doc, int hitId) {
        ZloMessage res = fromDocument(doc);
        res.setHitId(hitId);
        return res;
    }

    public static ZloMessage fromHit(Hit hit) {
        try {
            ZloMessage res = fromDocument(hit.getDocument());
            res.setHitId(hit.getId()); // to be adle to determine hit by msg
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Analyzer constructAnalyzer() {
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new KeywordAnalyzer());
        analyzer.addAnalyzer(FIELDS.TITLE, Config.ANALYZER);
        analyzer.addAnalyzer(FIELDS.BODY, Config.ANALYZER);
        return analyzer;
    }

    static final long serialVersionUID = -3231624250115810539L; // for deserialization
}
