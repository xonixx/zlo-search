package org.xonix.zlo.search.model;

import org.apache.lucene.search.Hit;

import java.util.Date;

/**
 * Author: Vovan
 * Date: 23.09.2007
 * Time: 16:49:51
 */
public class ZloMessageLazy implements ZloMessageAccessor {
    private ZloMessage msg = null;
    private Hit hit;

    public ZloMessageLazy(Hit hit) {
        this.hit = hit;
    }

    private void init() {
        if (msg == null) {
            msg = ZloMessage.fromHit(hit);
        }
    }

    public int getNum() {
        init();
        return msg.getNum();
    }

    public String getNick() {
        init();
        return msg.getNick();
    }

    public String getHost() {
        init();
        return msg.getHost();
    }

    public String getTopic() {
        init();
        return msg.getTopic();
    }

    public String getTitle() {
        init();
        return msg.getTitle();
    }

    public String getBody() {
        init();
        return msg.getBody();
    }

    public boolean isReg() {
        init();
        return msg.isReg();
    }

    public boolean isHasUrl() {
        init();
        return msg.isHasUrl();
    }

    public boolean isHasImg() {
        init();
        return msg.isHasImg();
    }

    public Date getDate() {
        init();
        return msg.getDate();
    }

    public ZloMessage getMessage() {
        init();
        return msg;
    }
}
