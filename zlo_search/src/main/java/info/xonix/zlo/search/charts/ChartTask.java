package info.xonix.zlo.search.charts;

import java.util.Date;
import java.util.List;

/**
 * User: xonix
 * Date: 8/20/14
 * Time: 8:49 PM
 */
public class ChartTask {
    public final String forumId;
    public final List<String> nicks;
    public final Date start;
    public final Date end;
    public final ChartType type;

    public ChartTask(String forumId, List<String> nicks, Date start, Date end, ChartType type) {
        this.forumId = forumId;
        this.nicks = nicks;
        this.start = start;
        this.end = end;
        this.type = type;
    }
}
