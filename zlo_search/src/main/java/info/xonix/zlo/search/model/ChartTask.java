package info.xonix.zlo.search.model;

import info.xonix.zlo.search.charts.ChartType;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private ChartType type;

    private String result;
    private String error;

    // TODO: why we need this?
    public ChartTask() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return StringUtils.join(nicks, '\n');
    }

    public void setDbNicks(String dbNicks) {
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

    @Override
    public String toString() {
        return "ChartTask{" +
                "id=" + id +
                ", forumId='" + forumId + '\'' +
                ", nicks=" + nicks +
                ", start=" + start +
                ", end=" + end +
                ", type=" + type +
                ", result='" + result + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
