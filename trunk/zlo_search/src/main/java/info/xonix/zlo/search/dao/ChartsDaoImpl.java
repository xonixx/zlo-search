package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.charts.ChartType;
import info.xonix.zlo.search.model.ChartTask;
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
    @Override
    public long insertChartTask(ChartTask task) {
        long id = newSimpleJdbcInsert()
                .withTableName("chart_task")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(new MapSqlParameterSource()
                        .addValue("forumId", task.getForumId())
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
    public void saveChartTaskResult(long id, String result) {
        getJdbcTemplate().update("update chart_task set result=? where id=?", result, id);
    }

    @Override
    public void saveChartTaskError(long id, String error) {
        getJdbcTemplate().update("update chart_task set error=? where id=?", error, id);
    }

    @Override
    public ChartTask loadChartTask(long id) {
        return getJdbcTemplate().queryForObject(
                "select * from chart_task where id=?",
                new RowMapper<ChartTask>() {
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

                        return chartTask;
                    }
                }
        );
    }
}
