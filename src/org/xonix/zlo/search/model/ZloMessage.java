package org.xonix.zlo.search.model;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hit;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.utils.HtmlUtils;

import java.text.ParseException;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.Serializable;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 20:18:10
 */
public class ZloMessage implements Serializable, ZloMessageAccessor {
    private static final String TRUE = "1";
    private static final String FALSE = "0";

    public static final String URL_NUM = "num";
    public static final String NICK = "nick";
    public static final String HOST = "host";
    public static final String TOPIC = "topic";
    public static final String TITLE_HTML = "titleHtml";
    public static final String TITLE = "title"; // clean - w/o html
    public static final String BODY_HTML = "bodyHtml";
    public static final String BODY = "body"; // clean - w/o html
    public static final String DATE = "date";
    public static final String REG = "reg";
    public static final String HAS_URL = "url";
    public static final String HAS_IMG = "img";

    private String nick;
    private String host;
    private String topic;
    private String title;
    private String body;
    private Date date;
    private boolean reg = false;
    private int num = -1; // default

    private String titleClean;
    private String bodyClean;
    private Boolean hasUrl;
    private Boolean hasImg;

    private int hitId;
    
    public static String [] TOPICS = {
        "Все темы", "Без темы",
        "Учеба", "Работа", "Мурзилка",
        "Обсуждение", "Новости", "Спорт",
        "Развлечения", "Движок борды", "Программирование",
        "Куплю", "Продам", "Услуги",
        "Windows", "BSD/Linux", "Проблемы сети",
        "Голосование", "Потеряно/Найдено", "Temp"
    };

    public static NumberFormat URL_NUM_FORMAT = new DecimalFormat("0000000000"); // 10 zeros

    public static Map<String, String> TOPIC_CODES = new HashMap<String, String>(TOPIC.length());

    static {
        TOPIC_CODES.put(TOPICS[0], "0"); // all topics ?? 
        TOPIC_CODES.put("", "1"); // wo topics
        for (int i=2; i<TOPICS.length; i++) {
            TOPIC_CODES.put(TOPICS[i], Integer.toString(i));
        }
    }

    public ZloMessage() {
    }

    public ZloMessage(String userName, String hostName, String msgTopic, String msgTitle, String msgBody, Date msgDate,
                      boolean reg, int urlNum,
                      Boolean hasUrl, Boolean hasImg) {
        this.nick = userName;
        this.host = hostName;
        this.topic = msgTopic;
        this.title = msgTitle;
        this.body = msgBody;
        this.date = msgDate;
        this.reg = reg;
        this.num = urlNum;
        this.hasUrl = hasUrl;
        this.hasImg = hasImg;
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

    public ZloMessage getMessage() {
        return this;
    }

    public String toString() {
        return new StringBuffer("ZloMessage(\n")
                .append("\t").append("num=").append(num).append(",\n")
                .append("\t").append("topic=").append(topic).append(",\n")
                .append("\t").append("title=").append(title).append(",\n")
                .append("\t").append("nick=").append(nick).append(",\n")
                .append("\t").append("reg=").append(reg).append(",\n")
                .append("\t").append("host=").append(host).append(",\n")
                .append("\t").append("date=").append(date).append(",\n")
                .append("\t").append("hasUrl=").append(isHasUrl() ? TRUE : FALSE).append(",\n")
                .append("\t").append("hasImg=").append(isHasImg() ? TRUE : FALSE).append(",\n")
                .append("\t").append("body=").append(body.replaceAll("\n","\n\t\t")).append("\n)").toString();
    }

    public Document getDocument() {
        Document doc = new Document();
        doc.add(new Field(URL_NUM, URL_NUM_FORMAT.format(num), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(TOPIC, TOPIC_CODES.get(topic), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(TITLE_HTML, title, Field.Store.YES, Field.Index.NO)); // "грязный" - храним, не индексируем
        doc.add(new Field(TITLE, getCleanTitle(), Field.Store.NO, Field.Index.TOKENIZED)); // "чистый" - индексируем, не храним
        doc.add(new Field(NICK, nick, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(REG, reg ? TRUE : FALSE, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(HOST, host, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(DATE, DateTools.dateToString(date, DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(BODY_HTML, body, Field.Store.COMPRESS, Field.Index.NO)); // "грязный" - храним сжатый, не индексируем
        doc.add(new Field(BODY, getCleanBody(), Field.Store.NO, Field.Index.TOKENIZED)); // "чистый" - индексируем, не храним
        doc.add(new Field(HAS_URL, isHasUrl() ? TRUE : FALSE, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(HAS_IMG, isHasImg() ? TRUE : FALSE, Field.Store.YES, Field.Index.UN_TOKENIZED));
        return doc;
    }

    public static ZloMessage fromDocument(Document doc) {
        try {
            return new ZloMessage(
                doc.get(NICK),
                doc.get(HOST),
                TOPICS[Integer.parseInt(doc.get(TOPIC))],
                doc.get(TITLE_HTML),
                doc.get(BODY_HTML),
                DateTools.stringToDate(doc.get(DATE)),
                TRUE.equals(doc.get(REG)),
                Integer.parseInt(doc.get(URL_NUM)),
                TRUE.equals(doc.get(HAS_URL)),
                TRUE.equals(doc.get(HAS_IMG))
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
        analyzer.addAnalyzer(TITLE, Config.ANALYZER);
        analyzer.addAnalyzer(BODY, Config.ANALYZER);
        return analyzer;
    }

    static final long serialVersionUID = -3231624250115810539L; // for deserialization
}
