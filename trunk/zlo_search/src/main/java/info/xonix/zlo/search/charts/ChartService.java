package info.xonix.zlo.search.charts;

import info.xonix.zlo.search.model.ChartTask;

import java.util.Map;

/**
 * User: xonix
 * Date: 8/22/14
 * Time: 4:03 PM
 */
public interface ChartService {

    /**
     * @param task chart task to render
     * @return Map&lt;String,Integer> or some other JSONable data object to build chart from
     */
    Object process(ChartTask task);

    long submitTask(ChartTask task);

    ChartTask loadChartTask(String descriptor);

    void processNextTask();
}
