package info.xonix.zlo.search.charts;

import info.xonix.zlo.search.model.ChartTask;

import java.util.Map;

/**
 * User: xonix
 * Date: 8/22/14
 * Time: 4:03 PM
 */
public interface ChartService {

    Map<String, Integer> process(ChartTask task);

    long submitTask(ChartTask task);

    void processNextTask();
}
