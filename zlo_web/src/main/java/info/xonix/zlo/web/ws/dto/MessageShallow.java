package info.xonix.zlo.web.ws.dto;

import java.util.Date;

/**
 * User: Vovan
 * Date: 19.12.10
 * Time: 23:12
 */
public class MessageShallow {
    protected Date date;
    protected int id = -1; // default

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }
}
