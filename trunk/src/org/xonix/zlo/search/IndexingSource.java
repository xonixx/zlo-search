package org.xonix.zlo.search;

import org.xonix.zlo.search.model.ZloMessage;

import java.util.List;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 16:48:24
 */
public interface IndexingSource {

    public ZloMessage getMessageByNumber(int num) throws DAO.Exception;
    public List<ZloMessage> getMessages(int from, int to) throws DAO.Exception;
    public int getLastMessageNumber() throws DAO.Exception;
}
