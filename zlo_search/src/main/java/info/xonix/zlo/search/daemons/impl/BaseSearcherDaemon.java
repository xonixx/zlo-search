package info.xonix.zlo.search.daemons.impl;

import info.xonix.utils.daemon.ForumIteratingDaemon;
import info.xonix.zlo.search.logic.exceptions.ExceptionsLogger;
import info.xonix.zlo.search.spring.AppSpringContext;

/**
 * User: gubarkov
 * Date: 28.04.12
 * Time: 19:15
 */
public abstract class BaseSearcherDaemon extends ForumIteratingDaemon {
    ExceptionsLogger exceptionsLogger = AppSpringContext.get(ExceptionsLogger.class);

    protected BaseSearcherDaemon(String forumId, int doPerTime, long sleepPeriod, long retryPeriod) {
        super(forumId, doPerTime, sleepPeriod, retryPeriod);
    }
}
