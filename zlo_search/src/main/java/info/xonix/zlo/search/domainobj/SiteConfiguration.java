package info.xonix.zlo.search.domainobj;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.utils.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;


/**
 * Author: Vovan
 * Date: 28.12.2007
 * Time: 2:45:21
 */
public abstract class SiteConfiguration {

    private static final Logger log = Logger.getLogger(SiteConfiguration.class);

    private String markEndMsg1;
    private String markEndMsg2;
    private String msgNotExistOrWrong;
    private String withoutTopic;

    // regexes
    private String msgRegReStr;
    private String msgUnregReStr;

    private Pattern msgRegRe;
    private Pattern msgUnregRe;

    private String linkIndexReStr;

    private Pattern linkIndexRe;

    private ArrayList<Integer> msgReGroupsOrder = null;

    private String siteUrl;
    private String siteCharset; // defined or NULL
    private String siteSmilesPath;
    private String siteDescription;

    private String readQuery;
    private String uinfoQuery;

    private String msgDatePattern;

    // index
    private boolean performIndexing;

    private int indexerIndexPerTime;
    private int indexerIndexPeriod;
    private int indexerReconnectPeriod;
    private int indexerLimitPerSecond;

    // db daemon
    private int dbScanPerTime;
    private int dbScanPeriod;
    private int dbReconnectPeriod;

    private int siteNumber;
    private int weight;

    private String name;

    private boolean noHost;


    public SiteConfiguration(String name) {
        Properties p = new Properties();

        final String propFile = name + ".properties";

        ok:
        {
            for (String possiblePath : new String[]{
                    Config.FORUMS_CONF_PATH + propFile,
                    Config.FORUMS_CONF_DEAD_PATH + propFile}) {

                if (Config.loadProperties(p, possiblePath)) {
                    break ok;
                }
            }

            throw new RuntimeException("Config for: " + name + " site was not found!");
        }

        this.name = name;

        initializeFromProperties(p);
    }

    private void initializeFromProperties(Properties p) {
        markEndMsg1 = p.getProperty("str.mark.end.1");
        markEndMsg2 = p.getProperty("str.mark.end.2");

        msgNotExistOrWrong = p.getProperty("str.msg.not.exists");
        withoutTopic = p.getProperty("str.without.topic");

        msgRegReStr = p.getProperty("regex.msg.reg");
        msgUnregReStr = p.getProperty("regex.msg.unreg");

        msgRegRe = msgRegReStr == null ? null : Pattern.compile(msgRegReStr, Pattern.DOTALL);
        msgUnregRe = msgUnregReStr == null ? null : Pattern.compile(msgUnregReStr, Pattern.DOTALL);

        String msgReGroups = p.getProperty("regex.msg.groups");
        if (msgReGroups != null) {
            msgReGroupsOrder = new ArrayList<Integer>();
            for (String s : StringUtils.split(msgReGroups, ',')) {
                msgReGroupsOrder.add(Integer.parseInt(s));
            }
        }

        linkIndexReStr = p.getProperty("regex.link.index");

        linkIndexRe = Pattern.compile(linkIndexReStr);

        siteUrl = p.getProperty("site.url");
        siteCharset = p.getProperty("site.charset");
        siteSmilesPath = p.getProperty("site.smiles.path");
        siteDescription = p.getProperty("site.description");

        readQuery = p.getProperty("site.query.read");
        uinfoQuery = p.getProperty("site.query.uinfo");

        // db -----
//        initDb(p);

        // indexer-----
        performIndexing = Config.isTrue(p.getProperty("indexer.perform.indexing"));

        indexerIndexPerTime = Integer.parseInt(p.getProperty("indexer.daemon.index.per.time"));
        indexerIndexPeriod = TimeUtils.parseToMilliSeconds(p.getProperty("indexer.daemon.period.to.index"));
        indexerReconnectPeriod = TimeUtils.parseToMilliSeconds(p.getProperty("indexer.daemon.period.to.reconnect"));
        indexerLimitPerSecond = Integer.parseInt(p.getProperty("indexer.limit.per.second"));

        // db daemon-----
        dbScanPerTime = Integer.parseInt(p.getProperty("db.daemon.scan.per.time"));
        dbScanPeriod = TimeUtils.parseToMilliSeconds(p.getProperty("db.daemon.period.to.scan"));
        dbReconnectPeriod = TimeUtils.parseToMilliSeconds(p.getProperty("db.daemon.period.to.reconnect"));

        siteNumber = Integer.parseInt(p.getProperty("site.number"));
        final String weightStr = p.getProperty("site.weight");
        weight = weightStr != null ? Integer.parseInt(weightStr) : Integer.MAX_VALUE;

        msgDatePattern = p.getProperty("str.date.pattern");

        noHost = Config.isTrue(p.getProperty("site.no.host", "0"));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SiteConfiguration)) {
            return false;
        }

        return /*StringUtils.equals(getName(), ((SiteConfiguration) obj).getName()) &&*/
                StringUtils.equals(name, ((SiteConfiguration) obj).name);
    }

    // getters

    public String getMarkEndMsg1() {
        return markEndMsg1;
    }

    public String getMarkEndMsg2() {
        return markEndMsg2;
    }

    public String getMsgNotExistOrWrong() {
        return msgNotExistOrWrong;
    }

    public String getWithoutTopic() {
        return withoutTopic;
    }

    public String getMsgRegReStr() {
        return msgRegReStr;
    }

    public String getMsgUnregReStr() {
        return msgUnregReStr;
    }

    public Pattern getMsgRegRe() {
        return msgRegRe;
    }

    public Pattern getMsgUnregRe() {
        return msgUnregRe;
    }

    public String getLinkIndexReStr() {
        return linkIndexReStr;
    }

    public Pattern getLinkIndexRe() {
        return linkIndexRe;
    }

    public ArrayList<Integer> getMsgReGroupsOrder() {
        return msgReGroupsOrder;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getSiteCharset() {
        return siteCharset;
    }

    public String getSiteSmilesPath() {
        return siteSmilesPath;
    }

    public String getSiteDescription() {
        return siteDescription;
    }

    public String getReadQuery() {
        return readQuery;
    }

    public String getUinfoQuery() {
        return uinfoQuery;
    }

    public String getMsgDatePattern() {
        return msgDatePattern;
    }

    public boolean isPerformIndexing() {
        return performIndexing;
    }

    public int getIndexerIndexPerTime() {
        return indexerIndexPerTime;
    }

    public int getIndexerIndexPeriod() {
        return indexerIndexPeriod;
    }

    public int getIndexerReconnectPeriod() {
        return indexerReconnectPeriod;
    }

    public int getIndexerLimitPerSecond() {
        return indexerLimitPerSecond;
    }

    public int getDbScanPerTime() {
        return dbScanPerTime;
    }

    public int getDbScanPeriod() {
        return dbScanPeriod;
    }

    public int getDbReconnectPeriod() {
        return dbReconnectPeriod;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public String getName() {
        return name;
    }

    public boolean isNoHost() {
        return noHost;
    }

    public int getWeight() {
        return weight;
    }
}
