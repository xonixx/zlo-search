package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.logic.exceptions.ExceptionCategory;
import info.xonix.zlo.search.logic.site.PageParseException;
import info.xonix.zlo.search.logic.site.RetrieverException;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * User: boost
 * Date: 28.09.2007
 * Time: 10:35:37
 */
public class DbDaemon extends Daemon {
    private static Logger logger = Logger.getLogger(DbDaemon.class);

    private AppLogic appLogic = AppSpringContext.get(AppLogic.class);
    private SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    private class DbProcess extends Process {
        @Override
        protected int getFromIndex() {
            return appLogic.getLastSavedMessageNumber(getSite());
        }

        @Override
        protected int getEndIndex() throws RetrieverException {
            return siteLogic.getLastMessageNumber(getSite());
        }

        @Override
        protected void perform(int from, int to) throws RetrieverException, PageParseException {
            Site site = getSite();
            appLogic.saveMessages(site, siteLogic.getMessages(site, from, to + 1));
            appLogic.setLastSavedDate(site, new Date());
        }

        protected boolean processException(Exception e) {
            exceptionsLogger.logException(e,
                    "Exception in db daemon: " + getSiteName(),
                    getClass(),
                    ExceptionCategory.DAEMON);

            return false;
        }

        protected void cleanUp() {
        }
    }

    public DbDaemon() {
        super();
        setParams();
    }

    protected DbDaemon(Site site) {
        super(site);
        setParams();
    }

    private void setParams() {
        setDoPerTime(getSite().getDbScanPerTime());
        setSleepPeriod(getSite().getDbScanPeriod());
        setRetryPeriod(getSite().getDbReconnectPeriod());
    }

    protected Process createProcess() {
        return new DbProcess();
    }

    public static void main(String[] args) {
        new DbDaemon().start();
    }
}