package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.model.ChartTask;

/**
 * User: xonix
 * Date: 8/22/14
 * Time: 3:31 PM
 */
public interface ChartsDao {
    long insertChartTask(ChartTask task);

    void saveChartTaskResult(long id, String result);
    void saveChartTaskError(long id, String error);

    ChartTask loadChartTask(long id);
}
