package info.xonix.zlo.web.controlsdata;

import info.xonix.zlo.web.controlsdata.pojos.stats.DaysNum;
import org.apache.commons.collections.map.DefaultedMap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: Vovan
 * Date: 22.01.11
 * Time: 17:40
 */
public class ControlsData {
    public final static Map<String, DaysNum> STATS_PERIODS_MAP = Collections.unmodifiableMap(createStatsPeriodsMap());

    @SuppressWarnings("unchecked")
    private static Map<String, DaysNum> createStatsPeriodsMap() {
        final Map<String, DaysNum> map = new LinkedHashMap<String, DaysNum>();

        map.put("1", new DaysNum(1, "сутки"));
        map.put("2", new DaysNum(2, "2-е суток"));
        map.put("3", new DaysNum(10, "10 суток"));
        map.put("4", new DaysNum(30, "30 суток"));

        return DefaultedMap.decorate(map, map.get("1"));
    }
}
