package info.xonix.zlo.search.model;

import java.util.Date;

/**
 * Author: Vovan
 * Date: 23.09.2007
 * Time: 16:51:54
 */
public interface ZloMessageAccessor {

    public int getNum();

    public String getNick();

    public String getHost();

    public String getTopic();

    public String getTitle();

    public String getBody();

    public boolean isReg();

    public boolean isHasUrl();

    public boolean isHasImg();

    public Date getDate();

    public Message getMessage();
}
