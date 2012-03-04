package info.xonix.zlo.search.config.forums;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.utils.ConfigUtils;
import info.xonix.zlo.search.utils.TimeUtils;

import java.util.Properties;

/**
 * The params of forum scanning/indexing
 * <p/>
 * User: gubarkov
 * Date: 29.02.12
 * Time: 18:04
 */
public class ForumParams {
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

    public ForumParams(String filePath) {
        init(ConfigUtils.loadProperties(filePath, "forum params"));
    }

    private void init(Properties p) {
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
}
