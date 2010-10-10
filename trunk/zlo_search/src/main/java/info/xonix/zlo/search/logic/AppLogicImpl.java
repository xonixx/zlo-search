package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.dao.DbDict;
import info.xonix.zlo.search.dao.DbDictFields;
import info.xonix.zlo.search.dao.DbManager;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.utils.Check;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;
import java.util.List;

/**
 * User: Vovan
 * Date: 21.06.2010
 * Time: 0:23:00
 */
public class AppLogicImpl implements AppLogic, InitializingBean {
    private final static Logger log = Logger.getLogger(AppLogicImpl.class);

    private DbDict dbDict;
    private DbManager dbManager;

    public void setDbDict(DbDict dbDict) {
        this.dbDict = dbDict;
    }

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(dbDict, "dbDict");
        Check.isSet(dbManager, "dbManager");
    }

    @Override
    public void setLastIndexedNumber(Site site, int num) {
        dbDict.setInt(site, DbDictFields.DB_DICT_LAST_INDEXED_DOUBLE, num);
        dbDict.setDate(site, DbDictFields.DB_DICT_LAST_INDEXED_DOUBLE_DATE, new Date());
    }

    @Override
    public int getLastIndexedNumber(Site site) {
        return dbDict.getInt(site, DbDictFields.DB_DICT_LAST_INDEXED_DOUBLE, 0);
    }

    @Override
    public Date getLastIndexedDate(Site site) {
        return dbDict.getDate(site, DbDictFields.DB_DICT_LAST_INDEXED_DOUBLE_DATE, new Date(0));
    }

    @Override
    public void setLastSavedDate(Site site, Date d) {
        dbDict.setDate(site, DbDictFields.DB_DICT_LAST_SAVED_DATE, d);
    }

    @Override
    public Date getLastSavedDate(Site site) {
        return dbDict.getDate(site, DbDictFields.DB_DICT_LAST_SAVED_DATE, new Date(0));
    }

    // messages

    @Override
    public void saveMessages(Site site, List<Message> msgs) {
        log.info(site.getName() + " - Saving (" + msgs.get(0).getNum() + " - " + msgs.get(msgs.size() - 1).getNum() + ") msgs to DB...");
        dbManager.saveMessagesFast(site, msgs);
        log.info(site.getName() + " - Successfully saved " + msgs.size() + " msgs to DB.");
    }

    @Override
    public Message getMessageByNumber(Site site, int num) {
        return dbManager.getMessageByNumber(site, num);
    }

    @Override
    public List<Message> getMessages(Site site, int start, int end) {
        return dbManager.getMessagesByRange(site, start, end);
    }

    @Override
    public int getLastSavedMessageNumber(Site site) {
        return dbManager.getLastMessageNumber(site);
    }
}
