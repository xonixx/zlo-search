package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.domain.ChartTask;

import java.util.List;

/**
 * User: xonix
 * Date: 8/22/14
 * Time: 3:31 PM
 */
public interface ChartsDao {
    long insertChartTask(ChartTask task);

    void updateChartTask(ChartTask task);

    ChartTask loadChartTask(long id);
    ChartTask loadChartTask(String descriptor);

    void removeTasksLessThen(long id);

    List<ChartTask> getLastTasks(int count);
}
