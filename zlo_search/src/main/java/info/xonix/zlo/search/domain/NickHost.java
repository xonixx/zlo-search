package info.xonix.zlo.search.domain;

/**
 * User: xonix
 * Date: 30.11.15
 * Time: 0:02
 */
public class NickHost {
    private String nick;
    private String host;
    private int cnt;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    @Override
    public String toString() {
        return "NickHost{" +
                "nick='" + nick + '\'' +
                ", host='" + host + '\'' +
                ", cnt=" + cnt +
                '}';
    }
}
