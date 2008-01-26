package org.xonix.zlo.search.dao;

import org.xonix.zlo.search.IndexingSource;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.db.DbManagerSource;
import org.xonix.zlo.search.model.ZloMessage;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Author: Vovan
* Date: 11.01.2008
* Time: 22:31:10
*/
public class DB extends DbManagerSource implements IndexingSource {
    private static Logger logger = Logger.getLogger(DB.class);

    public DB(Site site) {
        super(site);
    }

    public void saveMessages(List<ZloMessage> msgs) throws DAOException {
        saveMessages(msgs, true);
    }

    public void saveMessages(List<ZloMessage> msgs, boolean fast) throws DAOException {
        try {
            logger.info(getSiteName() + " - Saving (" + msgs.get(0).getNum() + " - " + msgs.get(msgs.size() - 1).getNum() + ") msgs to DB...");
            if (fast)
                getDbManager().saveMessagesFast(msgs);
            else {
                throw new UnsupportedOperationException();
            }
            logger.info(getSiteName() + " - Successfully saved " + msgs.size() + " msgs to DB.");
        } catch (DbException e) {
            throw new DAOException(this, e);
        }
    }

    public ZloMessage getMessageByNumber(int num) throws DAOException {
        try {
            return getDbManager().getMessageByNumber(num);
        } catch (DbException e) {
            throw new DAOException(this, e);
        }
    }

    public List<ZloMessage> getMessages(int start, int end) throws DAOException {
        try {
            return getDbManager().getMessagesByRange(start, end);
        } catch (DbException e) {
            throw new DAOException(this, e);
        }
    }

    public int getLastMessageNumber() throws DAOException {
        try {
            return getDbManager().getLastMessageNumber();
        } catch (DbException e) {
            throw new DAOException(this, e);
        }
    }
}
