package info.xonix.zlo.search;

import java.util.Observable;

/**
 * User: Vovan
 * Date: 21.09.2009
 * Time: 2:28:41
 * <p/>
 * Always changed => always fired
 */
public class TheObservable extends Observable {

    public TheObservable() {
        super();
        setChanged();
    }

    @Override
    protected void clearChanged() {
        setChanged();
    }
}
