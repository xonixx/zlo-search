package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.charts.ChartType;
import info.xonix.zlo.search.model.ChartTask;
import info.xonix.zlo.search.model.ChartTypeStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: xonix
 * Date: 8/22/14
 * Time: 3:32 PM
 */
public class ChartsDaoImpl extends DaoImplBase implements ChartsDao {
    private final RowMapper<ChartTask> CHART_TASK_ROWMAPPER = new RowMapper<ChartTask>() {
        @Override
        public ChartTask mapRow(ResultSet rs, int rowNum) throws SQLException {
            ChartTask chartTask = new ChartTask();

            chartTask.setId(rs.getLong("id"));
            chartTask.setForumId(rs.getString("forumId"));
            chartTask.setDbNicks(rs.getString("nicks"));
            chartTask.setStart(rs.getDate("start"));
            chartTask.setEnd(rs.getDate("end"));
            chartTask.setType(ChartType.valueOf(rs.getString("type")));
            chartTask.setResult(rs.getString("result"));
            chartTask.setError(rs.getString("error"));
            chartTask.setStatus(ChartTypeStatus.valueOf(rs.getString("status")));

            return chartTask;
        }
    };

    @Override
    public long insertChartTask(ChartTask task) {
        long id = newSimpleJdbcInsert()
                .withTableName("chart_task")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(new MapSqlParameterSource()
                        .addValue("forumId", task.getForumId())
                        .addValue("status", task.getStatus().name())
                        .addValue("descriptor", task.getDescriptor())
                        .addValue("nicks", task.getDbNicks())
                        .addValue("start", task.getStart())
                        .addValue("end", task.getEnd())
                        .addValue("type", task.getType().name())
                )
                .longValue();
        task.setId(id);
        return id;
    }

    @Override
    public void updateChartTask(ChartTask task) {
        getJdbcTemplate().update(
                "update chart_task " +
                        "set status=?, " +
                        "result=?, " +
                        "error=? " +
                        "where id=?",
                task.getStatus().name(),
                task.getResult(),
                task.getError(),
                task.getId());
    }

    @Override
    public ChartTask loadChartTask(long id) {
        try {
            return getJdbcTemplate().queryForObject(
                    "select * from chart_task where id=?",
                    CHART_TASK_ROWMAPPER,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public ChartTask loadChartTask(String descriptor) {
        try {
            return getJdbcTemplate().queryForObject(
                    "select * from chart_task where descriptor=?",
                    CHART_TASK_ROWMAPPER,
                    descriptor
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
