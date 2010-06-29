package info.xonix.zlo.search;

import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.model.Message;

import java.util.List;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 16:48:24
 */
@Deprecated
public interface IndexingSource {

    public Message getMessageByNumber(int num) throws DAOException;

    public List<Message> getMessages(int from, int to) throws DAOException;

    public int getLastMessageNumber() throws DAOException;
}
