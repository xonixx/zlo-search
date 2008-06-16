package info.xonix.zlo.search.site;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.db.DbAccessor;
import info.xonix.zlo.search.utils.TimeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Properties;
import java.util.ArrayList;


/**
 * Author: Vovan
 * Date: 28.12.2007
 * Time: 2:45:21
 */
public class SiteAccessor extends DbAccessor {

    public static final String SITE_CONFIG_PREFIX = "site.config.";

    private String MARK_END_MSG_1;
    private String MARK_END_MSG_2;
    private String MSG_NOT_EXIST_OR_WRONG;
    private String WITHOUT_TOPIC;

    // regexes
    private String MSG_REG_RE_STR;
    private String MSG_UNREG_RE_STR;
    private String LINK_INDEX_REGEX;

    private ArrayList<Integer> MSG_RE_GROUPS_ORDER = null;

    private String SITE_URL;
    private String SITE_DESCRIPTION;

    private String READ_QUERY;

    private String MSG_DATE_PATTERN;

    // index
    private boolean PERFORM_INDEXING;
    private String INDEX_DIR_DOUBLE;

    private int INDEXER_INDEX_PER_TIME;
    private int INDEXER_INDEX_PERIOD;
    private int INDEXER_RECONNECT_PERIOD;
    private int INDEXER_LIMIT_PER_SECOND;

    // db daemon
    private int DB_SCAN_PER_TIME;
    private int DB_SCAN_PERIOD;
    private int DB_RECONNECT_PERIOD;

    private int SITE_NUMBER;

    private String SITE_NAME;


    public SiteAccessor(String siteName) {
        Properties p = new Properties();

        try {
            for (String propFile : Config.getProp(SITE_CONFIG_PREFIX + siteName).split(";")) {
                Config.loadProperties(p, "info/xonix/zlo/search/config/" + propFile);
            }
        } catch (NullPointerException e) {
            System.err.println("Can't locate: " + SITE_CONFIG_PREFIX + siteName);
            throw e;
        }

        setName(siteName);

        MARK_END_MSG_1 = p.getProperty("str.mark.end.1");
        MARK_END_MSG_2 = p.getProperty("str.mark.end.2");

        MSG_NOT_EXIST_OR_WRONG = p.getProperty("str.msg.not.exists");
        WITHOUT_TOPIC = p.getProperty("str.without.topic");

        MSG_REG_RE_STR = p.getProperty("regex.msg.reg");
        MSG_UNREG_RE_STR = p.getProperty("regex.msg.unreg");

        String msgReGroups = p.getProperty("regex.msg.groups");
        if (msgReGroups != null) {
            MSG_RE_GROUPS_ORDER = new ArrayList<Integer>();
            for (String s : StringUtils.split(msgReGroups, ',')) {
                MSG_RE_GROUPS_ORDER.add(Integer.parseInt(s));    
            }
        }

        LINK_INDEX_REGEX = p.getProperty("regex.link.index");

        SITE_URL = p.getProperty("site.url");
        SITE_DESCRIPTION = p.getProperty("site.description");

        READ_QUERY = p.getProperty("site.read.query");

        // db -----
        initDb(p);

        // indexer-----
        PERFORM_INDEXING = Config.TRUE.equals(p.getProperty("indexer.perform.indexing"));
        INDEX_DIR_DOUBLE = p.getProperty("indexer.dir.double");

        INDEXER_INDEX_PER_TIME = Integer.parseInt(p.getProperty("indexer.daemon.index.per.time"));
        INDEXER_INDEX_PERIOD = TimeUtils.parseToMilliSeconds(p.getProperty("indexer.daemon.period.to.index"));
        INDEXER_RECONNECT_PERIOD = TimeUtils.parseToMilliSeconds(p.getProperty("indexer.daemon.period.to.reconnect"));
        INDEXER_LIMIT_PER_SECOND = Integer.parseInt(p.getProperty("indexer.limit.per.second"));

        // db daemon-----
        DB_SCAN_PER_TIME = Integer.parseInt(p.getProperty("db.daemon.scan.per.time"));
        DB_SCAN_PERIOD = TimeUtils.parseToMilliSeconds(p.getProperty("db.daemon.period.to.scan"));
        DB_RECONNECT_PERIOD = TimeUtils.parseToMilliSeconds(p.getProperty("db.daemon.period.to.reconnect"));

        SITE_NUMBER = Integer.parseInt(p.getProperty("site.number"));
        SITE_NAME = p.getProperty("site.name");

        MSG_DATE_PATTERN = p.getProperty("str.date.pattern");
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SiteAccessor)) {
            return false;
        }

        return StringUtils.equals(getName(), ((SiteAccessor) obj).getName()) &&
                StringUtils.equals(SITE_NAME, ((SiteAccessor) obj).SITE_NAME);
    }

    // getters & setters
    public String getMARK_END_MSG_1() {
        return MARK_END_MSG_1;
    }

    public void setMARK_END_MSG_1(String MARK_END_MSG_1) {
        this.MARK_END_MSG_1 = MARK_END_MSG_1;
    }

    public String getMARK_END_MSG_2() {
        return MARK_END_MSG_2;
    }

    public void setMARK_END_MSG_2(String MARK_END_MSG_2) {
        this.MARK_END_MSG_2 = MARK_END_MSG_2;
    }

    public String getMSG_NOT_EXIST_OR_WRONG() {
        return MSG_NOT_EXIST_OR_WRONG;
    }

    public void setMSG_NOT_EXIST_OR_WRONG(String MSG_NOT_EXIST_OR_WRONG) {
        this.MSG_NOT_EXIST_OR_WRONG = MSG_NOT_EXIST_OR_WRONG;
    }

    public String getWITHOUT_TOPIC() {
        return WITHOUT_TOPIC;
    }

    public void setWITHOUT_TOPIC(String WITHOUT_TOPIC) {
        this.WITHOUT_TOPIC = WITHOUT_TOPIC;
    }

    public String getMSG_REG_RE_STR() {
        return MSG_REG_RE_STR;
    }

    public void setMSG_REG_RE_STR(String MSG_REG_RE_STR) {
        this.MSG_REG_RE_STR = MSG_REG_RE_STR;
    }

    public String getMSG_UNREG_RE_STR() {
        return MSG_UNREG_RE_STR;
    }

    public void setMSG_UNREG_RE_STR(String MSG_UNREG_RE_STR) {
        this.MSG_UNREG_RE_STR = MSG_UNREG_RE_STR;
    }

    public String getLINK_INDEX_REGEX() {
        return LINK_INDEX_REGEX;
    }

    public void setLINK_INDEX_REGEX(String LINK_INDEX_REGEX) {
        this.LINK_INDEX_REGEX = LINK_INDEX_REGEX;
    }

    public ArrayList<Integer> getMSG_RE_GROUPS_ORDER() {
        return MSG_RE_GROUPS_ORDER;
    }

    public void setMSG_RE_GROUPS_ORDER(ArrayList<Integer> MSG_RE_GROUPS_ORDER) {
        this.MSG_RE_GROUPS_ORDER = MSG_RE_GROUPS_ORDER;
    }

    public String getSITE_URL() {
        return SITE_URL;
    }

    public void setSITE_URL(String SITE_URL) {
        this.SITE_URL = SITE_URL;
    }

    public String getSITE_DESCRIPTION() {
        return SITE_DESCRIPTION;
    }

    public void setSITE_DESCRIPTION(String SITE_DESCRIPTION) {
        this.SITE_DESCRIPTION = SITE_DESCRIPTION;
    }

    public String getREAD_QUERY() {
        return READ_QUERY;
    }

    public void setREAD_QUERY(String READ_QUERY) {
        this.READ_QUERY = READ_QUERY;
    }

    public String getMSG_DATE_PATTERN() {
        return MSG_DATE_PATTERN;
    }

    public boolean isPERFORM_INDEXING() {
        return PERFORM_INDEXING;
    }

    public void setPERFORM_INDEXING(boolean PERFORM_INDEXING) {
        this.PERFORM_INDEXING = PERFORM_INDEXING;
    }

    public String getINDEX_DIR_DOUBLE() {
        return INDEX_DIR_DOUBLE;
    }

    public void setINDEX_DIR_DOUBLE(String INDEX_DIR_DOUBLE) {
        this.INDEX_DIR_DOUBLE = INDEX_DIR_DOUBLE;
    }

    public int getINDEXER_INDEX_PER_TIME() {
        return INDEXER_INDEX_PER_TIME;
    }

    public void setINDEXER_INDEX_PER_TIME(int INDEXER_INDEX_PER_TIME) {
        this.INDEXER_INDEX_PER_TIME = INDEXER_INDEX_PER_TIME;
    }

    public int getINDEXER_INDEX_PERIOD() {
        return INDEXER_INDEX_PERIOD;
    }

    public void setINDEXER_INDEX_PERIOD(int INDEXER_INDEX_PERIOD) {
        this.INDEXER_INDEX_PERIOD = INDEXER_INDEX_PERIOD;
    }

    public int getINDEXER_RECONNECT_PERIOD() {
        return INDEXER_RECONNECT_PERIOD;
    }

    public void setINDEXER_RECONNECT_PERIOD(int INDEXER_RECONNECT_PERIOD) {
        this.INDEXER_RECONNECT_PERIOD = INDEXER_RECONNECT_PERIOD;
    }

    public int getINDEXER_LIMIT_PER_SECOND() {
        return INDEXER_LIMIT_PER_SECOND;
    }

    public void setINDEXER_LIMIT_PER_SECOND(int INDEXER_LIMIT_PER_SECOND) {
        this.INDEXER_LIMIT_PER_SECOND = INDEXER_LIMIT_PER_SECOND;
    }

    public int getDB_SCAN_PER_TIME() {
        return DB_SCAN_PER_TIME;
    }

    public void setDB_SCAN_PER_TIME(int DB_SCAN_PER_TIME) {
        this.DB_SCAN_PER_TIME = DB_SCAN_PER_TIME;
    }

    public int getDB_SCAN_PERIOD() {
        return DB_SCAN_PERIOD;
    }

    public void setDB_SCAN_PERIOD(int DB_SCAN_PERIOD) {
        this.DB_SCAN_PERIOD = DB_SCAN_PERIOD;
    }

    public int getDB_RECONNECT_PERIOD() {
        return DB_RECONNECT_PERIOD;
    }

    public void setDB_RECONNECT_PERIOD(int DB_RECONNECT_PERIOD) {
        this.DB_RECONNECT_PERIOD = DB_RECONNECT_PERIOD;
    }

    public int getSITE_NUMBER() {
        return SITE_NUMBER;
    }

    public void setSITE_NUMBER(int SITE_NUMBER) {
        this.SITE_NUMBER = SITE_NUMBER;
    }

    public String getSITE_NAME() {
        return SITE_NAME;
    }

    public void setSITE_NAME(String SITE_NAME) {
        this.SITE_NAME = SITE_NAME;
    }
}
