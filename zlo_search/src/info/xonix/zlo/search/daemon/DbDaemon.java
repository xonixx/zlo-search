package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.model.Site;
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

    protected Logger getLogger() {
        return logger;
    }

    private class DbProcess extends Process {
        public DbProcess() {
            super();
        }

        protected int getFromIndex() {
            return appLogic.getLastSavedMessageNumber(getSite());
        }

        protected int getEndIndex() {
            return siteLogic.getLastMessageNumber(getSite());
        }

        protected void perform(int from, int to) {
            Site site = getSite();
            appLogic.saveMessages(site, siteLogic.getMessages(site, from, to + 1));
            appLogic.setLastSavedDate(site, new Date());
        }

        protected boolean processException(Exception e) {
            // TODO!!!
/*            if (e instanceof DbException) {
                logger.warn(getSiteName() + " - Problem with db: " + e.getClass(), e);
                return true;
            } else if (e instanceof DAOException) {
                if (e.getCause() instanceof ConnectException) {
                    logger.error(getSiteName() + " - Problem with site... " + e.getCause().getClass().getName());
                } else if (e.getCause() instanceof DbException && e.getCause().getCause() instanceof BatchUpdateException) {
                    logger.error(getSiteName(), e);
                    logger.info("Resetting...");
                    reset();
                } else {
                    logger.error(getSiteName(), e);
                }
                return true;
            }*/

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