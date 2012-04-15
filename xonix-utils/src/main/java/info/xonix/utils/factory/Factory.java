package info.xonix.utils.factory;

import java.util.*;

/**
 * User: Vovan
 * Date: 29.06.2010
 * Time: 21:08:09
 */
public abstract class Factory<Key, Res> {
    private Map<Key, Res> modelToRes = new HashMap<Key, Res>();

    protected abstract Res create(Key key);

    public Collection<Res> all() {
        return modelToRes.values();
    }

    public Res get(Key key) {
        if (!modelToRes.containsKey(key)) {
            modelToRes.put(key, create(key));
        }

        return modelToRes.get(key);
    }

    public void reset() {
        final List<Key> keys = new ArrayList<Key>(modelToRes.keySet());
        for (Key key : keys) {
            reset(key);
        }
    }

    public void reset(Key key) {
        close(key, modelToRes.get(key));
        modelToRes.remove(key);
    }

    protected void close(Key key, Res res) {
        // by default do nothing, may override
    }
}
