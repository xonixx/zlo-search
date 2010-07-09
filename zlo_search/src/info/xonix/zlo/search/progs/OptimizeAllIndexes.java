package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.ZloObservable;
import info.xonix.zlo.search.daemon.Daemon;
import info.xonix.zlo.search.daemon.DaemonLauncher;
import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.spring.AppSpringContext;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Author: Vovan
 * Date: 24.03.2008
 * Time: 0:53:31
 */
public class OptimizeAllIndexes {
    private SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);
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

    public void go() throws IOException {
        OptimizeIndex optimizeIndex = new OptimizeIndex();
        for (Site site : siteLogic.getSites()) {
            if (site.isPerformIndexing()) {
                System.out.println("-----" + site.getName() + "-----");
                optimizeIndex.optimizeDoubleIndexForSite(site);
            }
        }
        observable.notifyObservers("optimized");
    }

    public void optimizeRestartDaemons() {
        final OptimizeAllIndexes optimizeAllIndexes = this;
        Daemon.on(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if ("exited".equals(arg)) {
                    Daemon.un(this);
                    System.out.println("Starting optimize...");

                    optimizeAllIndexes.on(this);
                    try {
                        optimizeAllIndexes.go();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if ("optimized".equals(arg)) {
                    optimizeAllIndexes.un(this);

                    System.out.println("Optimized... Starting daemons...");

                    new DaemonLauncher().main(new String[0]);
                }
            }
        });
        Daemon.setExitingAll();
    }
}
