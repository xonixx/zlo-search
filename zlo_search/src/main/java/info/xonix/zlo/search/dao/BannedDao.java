package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.domain.BannedStatus;

import javax.annotation.Nonnull;

/**
 * User: gubarkov
 * Date: 19.04.12
 * Time: 20:19
 */
public interface BannedDao {
    /**
     * @param ip IP to check
     * @return banned status of this IP
     */
    @Nonnull
    BannedStatus getBannedStatus(String ip);
}
