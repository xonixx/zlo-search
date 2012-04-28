package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.TheObservable;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.logic.ForumLogic;
import info.xonix.zlo.search.logic.IndexerLogic;
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

    private ForumLogic forumLogic = AppSpringContext.get(ForumLogic.class);
    private IndexerLogic indexerLogic = AppSpringContext.get(IndexerLogic.class);

    private Observable observable = new TheObservable();

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
        for (String forumId : GetForum.ids()) {
            if (GetForum.params(forumId).isPerformIndexing()) {
                optimizeIndex.optimizeDoubleIndexForSite(forumId);
            }
        }
        observable.notifyObservers("optimized");
    }

    public void optimizeRestartDaemons() {
/*        final OptimizeAllIndexes optimizeAllIndexes = this;

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
        }*/
    }
}
