package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.dao.BannedDao;
import info.xonix.zlo.search.domain.BannedStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;

/**
 * User: gubarkov
 * Date: 19.04.12
 * Time: 20:37
 */
public class UserLogicImpl implements UserLogic {
    @Autowired
    private BannedDao bannedDao;

    @Nonnull
    @Override
    public BannedStatus getBannedStatus(String ip) {
        return bannedDao.getBannedStatus(ip);
    }
}
