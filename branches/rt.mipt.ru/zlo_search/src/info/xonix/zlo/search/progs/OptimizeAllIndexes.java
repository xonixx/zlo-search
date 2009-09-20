package info.xonix.zlo.search.progs;

import info.xonix.zlo.search.daemon.Daemon;
import info.xonix.zlo.search.daemon.DaemonLauncher;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.ZloObservable;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Author: Vovan
 * Date: 24.03.2008
 * Time: 0:53:31
 */
public class OptimizeAllIndexes {
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
        for (Site site : Site.getSites()) {
            if (site.isPERFORM_INDEXING()) {
                System.out.println("-----" + site.getName() + "-----");
                OptimizeIndex.optimizeDoubleIndexForSite(site);
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

                    DaemonLauncher.main(new String[0]);
                }
            }
        });
        Daemon.setExitingAll();
    }
}
