package info.xonix.zlo.search.test.junit;

import info.xonix.zlo.search.logic.analysis.AnalysisLogic;
import info.xonix.zlo.search.logic.analysis.BotStatus;
import info.xonix.zlo.search.model.NickHost;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.junit.Test;

/**
 * User: xonix
 * Date: 30.11.15
 * Time: 0:19
 */
public class TestBotAnalysis {
    private final AnalysisLogic analysisLogic = AppSpringContext.get(AnalysisLogic.class);

    @Test
    public void test1() {
        BotStatus botStatus = analysisLogic.determineBotStatus("zlo", "xonix");

        System.out.println("First msg date: " + botStatus.firstMsgDate);
        System.out.println("Msgs: " + botStatus.msgsCnt);
        System.out.println("Politics: " + botStatus.politicsMsgsCnt);
        System.out.println("Hosts: ");

        for (NickHost host : botStatus.hosts) {
            System.out.println(host);
        }

    }
}
