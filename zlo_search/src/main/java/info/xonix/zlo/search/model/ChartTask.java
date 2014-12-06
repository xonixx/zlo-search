package info.xonix.zlo.search.model;

import info.xonix.utils.Util;
import info.xonix.zlo.search.charts.ChartType;
import info.xonix.zlo.search.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * User: xonix
 * Date: 8/20/14
 * Time: 8:49 PM
 */
public class ChartTask {
    private long id;

    private String forumId;
    private List<String> nicks;
    private Date start;
    private Date end;
    private List<String> searchQueries;
    private ChartType type;

    private String result;
    private String error;

    private ChartTaskStatus status;

    /**
     * will serve as unique key in db
     */
    public String getDescriptor() {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("forumId", forumId);
        map.put("dbNicks", getDbNicks());
        map.put("start", start);
        map.put("end", end);
        map.put("searchQueries", getDbSearchQueries());
        map.put("type", type);
        Util.removeNullValues(map);
        return JsonUtil.toJson(map);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getSearchQueries() {
        return searchQueries;
    }

    public void setSearchQueries(List<String> searchQueries) {
        this.searchQueries = searchQueries;
    }

    public String getDbSearchQueries() {
        if (searchQueries == null)
            return null;
        return StringUtils.join(searchQueries, '\n');
    }

    public void setDbSearchQueries(String queries) {
        if (StringUtils.isEmpty(queries))
            return;
        this.searchQueries = Arrays.asList(StringUtils.split(queries, '\n'));;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public List<String> getNicks() {
        return nicks;
    }

    public void setNicks(List<String> nicks) {
        this.nicks = nicks;
    }

    public String getDbNicks() {
        if (nicks == null)
            return null;
        return StringUtils.join(nicks, '\n');
    }

    public void setDbNicks(String dbNicks) {
        if (StringUtils.isEmpty(dbNicks))
            return;
        this.nicks = Arrays.asList(StringUtils.split(dbNicks, '\n'));
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ChartTaskStatus getStatus() {
        return status;
    }

    public void setStatus(ChartTaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ChartTask{" +
                "id=" + id +
                ", forumId='" + forumId + '\'' +
                ", nicks=" + nicks +
                ", start=" + start +
                ", end=" + end +
                ", searchQueries=" + searchQueries +
                ", type=" + type +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", status=" + status +
                '}';
    }
}
