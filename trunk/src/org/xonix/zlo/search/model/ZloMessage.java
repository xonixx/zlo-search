package org.xonix.zlo.search.model;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.xonix.zlo.search.Config;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.text.ParseException;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 20:18:10
 */
public class ZloMessage {
    public static final String URL_NUM = "num";
    public static final String NICK = "nick";
    public static final String HOST = "host";
    public static final String TOPIC = "topic";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String DATE = "date";
    public static final String REG = "reg";

    private String nick;
    private String host;
    private String topic;
    private String title;
    private String body;
    private Date date;
    private boolean reg = false;
    private int num = -1; // default
    
    public static String [] TOPICS = {
        "Все темы", "Без темы",
        "Учеба", "Работа", "Мурзилка",
        "Обсуждение", "Новости", "Спорт",
        "Развлечения", "Движок борды", "Программирование",
        "Куплю", "Продам", "Услуги",
        "Windows", "BSD/Linux", "Проблемы сети",
        "Голосование", "Потеряно/Найдено", "Temp"
    };

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
                      boolean reg, int urlNum) {
        this.nick = userName;
        this.host = hostName;
        this.topic = msgTopic;
        this.title = msgTitle;
        this.body = msgBody;
        this.date = msgDate;
        this.reg = reg;
        this.num = urlNum;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String toString() {
        return new StringBuffer("ZloMessage(\n")
                .append("\t").append("num=").append(num).append(",\n")
                .append("\t").append("topic=").append(topic).append(",\n")
                .append("\t").append("title=").append(title).append(",\n")
                .append("\t").append("nick=").append(nick).append(",\n")
                .append("\t").append("reg=").append(reg).append(",\n")
                .append("\t").append("host=").append(host).append(",\n")
                .append("\t").append("date=").append(date).append(",\n")
                .append("\t").append("body=").append(body.replaceAll("\n","\n\t\t")).append("\n)").toString();
    }

    public Document getDocument() {
        Document doc = new Document();
        doc.add(new Field(URL_NUM, Integer.toString(num), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(TOPIC, TOPIC_CODES.get(topic), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(TITLE, title, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field(NICK, nick, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(REG, Boolean.toString(reg), Field.Store.YES, Field.Index.NO));
        doc.add(new Field(HOST, host, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(DATE, DateTools.dateToString(date, DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field(BODY, body, Field.Store.COMPRESS, Field.Index.TOKENIZED));
        return doc;
    }

    public static ZloMessage fromDocument(Document doc) {
        ZloMessage result = null;
        try {
            result = new ZloMessage(
                doc.get(NICK), doc.get(HOST), TOPICS[Integer.parseInt(doc.get(TOPIC))], doc.get(TITLE), doc.get(BODY),
                DateTools.stringToDate(doc.get(DATE)), "true".equals(doc.get(REG)), Integer.parseInt(doc.get(URL_NUM))
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Analyzer constructAnalyzer() {
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(Config.ANALYZER);
        analyzer.addAnalyzer(TOPIC, new KeywordAnalyzer());
        analyzer.addAnalyzer(NICK, new KeywordAnalyzer());
        analyzer.addAnalyzer(HOST, new KeywordAnalyzer());
        analyzer.addAnalyzer(URL_NUM, new KeywordAnalyzer());
        analyzer.addAnalyzer(DATE, new KeywordAnalyzer());
        return analyzer;
    }
}
