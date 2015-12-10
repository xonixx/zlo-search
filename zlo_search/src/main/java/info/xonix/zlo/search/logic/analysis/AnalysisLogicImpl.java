package info.xonix.zlo.search.logic.analysis;

import info.xonix.zlo.search.dao.AppDao;
import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.domain.Message;
import info.xonix.zlo.search.domain.NickHost;
import info.xonix.zlo.search.logic.MessageFields;
import info.xonix.zlo.search.logic.SearchLogic;
import info.xonix.zlo.search.utils.IOUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: xonix
 * Date: 29.11.15
 * Time: 22:17
 */
public class AnalysisLogicImpl implements AnalysisLogic {
    @Autowired
    private SearchLogic searchLogic;
    @Autowired
    private MessagesDao messagesDao;
    @Autowired
    private AppDao appDao;

    private static final List<String> politicsList = preparePoliticsList();

    private static List<String> preparePoliticsList() {
        return IOUtil.loadStrings(AnalysisLogicImpl.class.getResourceAsStream("/politics.txt"));
    }

    /**
     * @return bot status || null if nick not found
     */
    @Override
    public BotStatus determineBotStatus(String forumId, String nick) {
        String nickSearchStr = MessageFields.NICK + ":(\"" + nick + "\")";

        int[] nickIds = searchLogic.search(forumId,
                nickSearchStr,
                0, 1000);

        int messagesCnt = nickIds.length;

        if (messagesCnt == 0)
            return null;

        Arrays.sort(nickIds); // TODO do we need to sort or already sorted?

        int youngestId = nickIds[0];

        Message youngestMsg = messagesDao.getMessageByNumber(forumId, youngestId);
        Date firstMsgDate = youngestMsg.getDate();

        String politicsSearchPart = StringUtils.join(politicsList, " OR ");
        String politicsSearch = nickSearchStr +
                " (" +
                MessageFields.TITLE +
                ":(" +
                politicsSearchPart +
                ") OR " +
                MessageFields.BODY +
                ":(" +
                politicsSearchPart +
                "))";

        int[] nickPoliticsIds = searchLogic.search(forumId,
                politicsSearch,
                0, 1000);

        int politicsMsgsCnt = nickPoliticsIds.length;

        List<NickHost> hosts = appDao.getHosts(forumId, nick);

        return new BotStatus(firstMsgDate, hosts, messagesCnt, politicsMsgsCnt);
    }
}
