package info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf;

import info.xonix.zlo.search.config.Config;
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
public class WwwconfParams {

    private static final Logger log = Logger.getLogger(WwwconfParams.class);

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

    private String forumId;

    private boolean noHost;


    public WwwconfParams(String forumId) {
        Properties p = new Properties();

        final String propFile = forumId + ".properties";

        ok:
        {
            for (String possiblePath : new String[]{
                    Config.FORUMS_CONF_PATH + propFile,
                    Config.FORUMS_CONF_DEAD_PATH + propFile}) {

                if (Config.loadProperties(p, possiblePath)) {
                    break ok;
                }
            }

            throw new RuntimeException("Config for: " + forumId + " site was not found!");
        }

        this.forumId = forumId;

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

        msgDatePattern = p.getProperty("str.date.pattern");

        noHost = Config.isTrue(p.getProperty("site.no.host", "0"));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WwwconfParams)) {
            return false;
        }

        return /*StringUtils.equals(getName(), ((WwwconfParams) obj).getName()) &&*/
                StringUtils.equals(forumId, ((WwwconfParams) obj).forumId);
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

/*    public boolean isPerformIndexing() {
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
    }*/

    public String getForumId() {
        return forumId;
    }

    public boolean isNoHost() {
        return noHost;
    }

/*    public int getWeight() {
        return weight;
    }*/
}
