package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.domain.BannedStatus;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: gubarkov
 * Date: 19.04.12
 * Time: 20:25
 */
public class BannedDaoImpl extends DaoImplBase implements BannedDao {
    @Nonnull
    @Override
    public BannedStatus getBannedStatus(String ip) {
        final List<String> reasons = getJdbcTemplate().queryForList(
                "SELECT reason FROM banned WHERE ip=?", String.class, ip);

        return reasons.isEmpty()
                ? BannedStatus.NOT_BANNED
                : BannedStatus.banned(reasons.get(0));
    }
}
