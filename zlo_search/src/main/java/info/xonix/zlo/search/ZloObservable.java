package info.xonix.zlo.search;

import java.util.Observable;

/**
 * User: Vovan
 * Date: 21.09.2009
 * Time: 2:28:41
 * <p/>
 * Always changed => always fired
 */
public class ZloObservable extends Observable {

    public ZloObservable() {
        super();
        setChanged();
    }

    @Override
    protected void clearChanged() {
        setChanged();
    }
}
