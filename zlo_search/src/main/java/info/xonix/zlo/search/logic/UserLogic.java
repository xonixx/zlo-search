package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.domain.BannedStatus;

import javax.annotation.Nonnull;

/**
 * User: gubarkov
 * Date: 19.04.12
 * Time: 20:28
 */
public interface UserLogic {
    /**
     * @param ip IP to check
     * @return banned status of this IP
     */
    @Nonnull
    public BannedStatus getBannedStatus(String ip);
}
