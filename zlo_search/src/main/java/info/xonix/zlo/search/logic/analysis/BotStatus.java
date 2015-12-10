package info.xonix.zlo.search.logic.analysis;

import info.xonix.zlo.search.domain.NickHost;

import java.util.Date;
import java.util.List;

/**
 * User: xonix
 * Date: 29.11.15
 * Time: 22:19
 */
public class BotStatus {
    public final Date firstMsgDate;
    public final List<NickHost> hosts;
    public final int msgsCnt;
    public final int politicsMsgsCnt;

    public BotStatus(Date firstMsgDate, List<NickHost> hosts, int msgsCnt, int politicsMsgsCnt) {
        this.firstMsgDate = firstMsgDate;
        this.hosts = hosts;
        this.msgsCnt = msgsCnt;
        this.politicsMsgsCnt = politicsMsgsCnt;
    }

    @Override
    public String toString() {
        return "BotStatus{" +
                "firstMsgDate=" + firstMsgDate +
                ", hosts=" + hosts +
                ", msgsCnt=" + msgsCnt +
                ", politicsMsgsCnt=" + politicsMsgsCnt +
                '}';
    }
}
