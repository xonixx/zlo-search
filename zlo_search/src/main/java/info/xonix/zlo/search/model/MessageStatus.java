package info.xonix.zlo.search.model;

/**
 * User: Vovan
 * Date: 29.06.2010
 * Time: 21:55:10
 */
public enum MessageStatus {
    OK,
    DELETED,
    NOT_EXISTS
//    SPAM,
//    UNKNOWN,
    ;

    //    TODO: !!!this is highly incorrect!!!
    public static MessageStatus fromInt(int id) {
        return MessageStatus.values()[id];
    }

    //    TODO: !!!this is highly incorrect!!!
    public int getInt() {
        for (int i = 0; i < MessageStatus.values().length; i++) {
            if (this == MessageStatus.values()[i])
                return i;
        }
        return -1;
    }
}
