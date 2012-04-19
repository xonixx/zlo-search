package info.xonix.zlo.search.domain;

/**
 * User: gubarkov
 * Date: 19.04.12
 * Time: 20:21
 */
public class BannedStatus {
    private final boolean banned;
    private String reason;

    public final static BannedStatus NOT_BANNED = new BannedStatus(false, null);

    private BannedStatus(boolean banned, String reason) {
        this.banned = banned;
        this.reason = reason;
    }

    public static BannedStatus banned(String reason) {
        return new BannedStatus(true, reason);
    }

    public boolean isBanned() {
        return banned;
    }

    public boolean isNotBanned() {
        return !isBanned();
    }

    public String getReason() {
        return reason;
    }
}
