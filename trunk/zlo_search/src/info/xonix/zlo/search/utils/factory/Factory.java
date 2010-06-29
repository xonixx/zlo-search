package info.xonix.zlo.search.utils.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Vovan
 * Date: 29.06.2010
 * Time: 21:08:09
 */
public abstract class Factory<Model, Res> {
    private Map<Model, Res> modelToRes = new HashMap<Model, Res>();

    protected abstract Res create(Model model);

    public Res get(Model model) {
        if (!modelToRes.containsKey(model)) {
            modelToRes.put(model, create(model));
        }

        return modelToRes.get(model);
    }
}
