package info.xonix.zlo.search.daemons.impl;

import info.xonix.utils.daemon.Daemon;
import info.xonix.utils.daemon.DaemonBase;
import info.xonix.utils.daemon.DaemonState;
import info.xonix.zlo.search.charts.ChartService;
import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.logic.ForumLogic;
import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * User: xonix
 */
public class RefetchParentsDaemon extends DaemonBase implements Daemon {
    public static final int PARENT_MISSING_ID = -1;
    public static final int PROCESS_PER_TIME = 100;

    private static final Logger log = Logger.getLogger(RefetchParentsDaemon.class);

    private MessagesDao messagesDao = AppSpringContext.get(MessagesDao.class);
    private ForumLogic forumLogic = AppSpringContext.get(ForumLogic.class);

    private String forumId;

    public RefetchParentsDaemon(String forumId) {
        super("Refetch Parents");
        this.forumId = forumId;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public void perform() {
        setStateIfNotExiting(DaemonState.PERFORMING);

        while (true) {
            if (isExiting())
                break;

            List<Integer> messageIds = messagesDao.getMessageIdsByMissingParent(forumId, PARENT_MISSING_ID, PROCESS_PER_TIME);
            if (messageIds.isEmpty()) {
                log.info("All parents refetched. Exiting.");
                setExiting();
                break;
            }

            Collections.sort(messageIds);
            log.debug("Will refetch parents for: " + messageIds);

            for (Integer messageId : messageIds) {
                try {
                    Message message = forumLogic.getMessageByNumber(forumId, messageId);
                    messagesDao.updateParent(forumId, messageId, message.getParentNum());
                } catch (ForumAccessException e) {
                    log.warn("Unable to fetch msg", e);
                }
            }
        }
    }
}
