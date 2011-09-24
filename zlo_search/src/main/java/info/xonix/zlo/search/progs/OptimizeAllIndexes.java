package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.ZloObservable;
import info.xonix.zlo.search.daemon.Daemon;
import info.xonix.zlo.search.daemon.DaemonLauncher;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.IndexerLogic;
import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Author: Vovan
 * Date: 24.03.2008
 * Time: 0:53:31
 */
public class OptimizeAllIndexes {
    private final static Logger log = Logger.getLogger(OptimizeAllIndexes.class);

    private SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);
    private IndexerLogic indexerLogic = AppSpringContext.get(IndexerLogic.class);

    private Observable observable = new ZloObservable();

    public void on(Observer o) {
        observable.addObserver(o);
    }

    public void un(Observer o) {
        observable.deleteObserver(o);
    }

    public static void main(String[] args) throws IOException {
        new OptimizeAllIndexes().go();
    }

    public void go() {
        final OptimizeIndex optimizeIndex = new OptimizeIndex();
        for (Site site : siteLogic.getSites()) {
            if (site.isPerformIndexing()) {
                optimizeIndex.optimizeDoubleIndexForSite(site);
            }
        }
        observable.notifyObservers("optimized");
    }

    public void optimizeRestartDaemons() {
        final OptimizeAllIndexes optimizeAllIndexes = this;

        if (Daemon.getDaemons().size() > 0) {
            Daemon.on(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if ("exited".equals(arg)) {
                        Daemon.un(this);
                        System.out.println("Starting optimize...");

                        optimizeAllIndexes.on(this);

                        log.info("Closing all index writers before optimize...");
                        indexerLogic.closeIndexWriters();

                        optimizeAllIndexes.go();
                    } else if ("optimized".equals(arg)) {
                        optimizeAllIndexes.un(this);

                        log.info("Optimized... Starting daemons...");

                        new DaemonLauncher().main(new String[0]);
                    }
                }
            });
            Daemon.setExitingAll();
        } else {
            go();
        }
    }
}
