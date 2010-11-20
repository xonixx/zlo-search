package info.xonix.zlo.search.daemon;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.model.Message;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Author: Vovan
 * Date: 05.04.2008
 * Time: 21:19:09
 */
public class DbMessagesPrintDaemon extends DbMessagesIteratingDaemon {
    private static final Logger logger = Logger.getLogger(DbMessagesPrintDaemon.class);

    protected DbMessagesPrintDaemon(Site site) {
        super(site);
    }

    public DbMessagesPrintDaemon(Site site, int perTime) {
        this(site);
        setDoPerTime(perTime);
    }

    protected void doWithMessages(List<Message> msgs) {
        for (Message m : msgs) {
            logger.info(m);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected String getIteratingVariableName() {
        return "lastPrintIter";
    }

    public static void main(String[] args) {
        DbMessagesPrintDaemon d = new DbMessagesPrintDaemon(Site.forName("zlo"), 10);
        d.reset();
        d.start();
    }
}
