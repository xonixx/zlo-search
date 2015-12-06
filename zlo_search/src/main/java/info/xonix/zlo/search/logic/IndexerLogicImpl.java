package info.xonix.zlo.search.logic;

import info.xonix.utils.Check;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.index.IndexManager;
import info.xonix.zlo.search.model.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Author: gubarkov
 * Date: 01.06.2007
 * Time: 1:07:38
 * TODO: handle clean-up
 */
public class IndexerLogicImpl implements IndexerLogic, InitializingBean {
    private static Logger log = Logger.getLogger(IndexerLogicImpl.class);

    public static NumberFormat URL_NUM_FORMAT = new DecimalFormat("0000000000"); // 10 zeros
    public static final String TRUE = "1";
    public static final String FALSE = "0";

    @Autowired
    private Config config;

    @Autowired
    private AppLogic appLogic;

    @Autowired
    private SearchLogic searchLogic;

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
        Check.isSet(appLogic, "appLogic");
        Check.isSet(searchLogic, "searchLogic");
    }

    private IndexWriter getWriter(String forumId) throws IOException {
        return IndexManager.get(forumId).getWriter();
    }

    private void addMessagesToIndex(String forumId, int start, int end) throws IndexerException {
        try {
            final IndexWriter writer = getWriter(forumId);

            for (Message msg : appLogic.getMessages(forumId, start, end)) {
                if (msg.isOk()) {
                    log.debug(forumId + " - Addind: " + (config.isDebug() ? msg : msg.getNum()));

                    writer.addDocument(messageToDocument(forumId, msg));
                } else {
                    log.debug(forumId + " - Not adding: " + msg.getNum() + " with status: " + msg.getStatus());
                }
            }
//            writer.flush();
            // committing changes (flush() will rollback at next write)
            writer.commit();
        } catch (IOException e) {
            log.error("Exception while adding to index", e);
            throw new IndexerException(e);
        }
    }

    private Document messageToDocument(String forumId, @Nonnull Message msg) {
        if (msg == null || !msg.isOk()) {
            throw new IllegalArgumentException("msg");
        }

        final String hostLowerCase = msg.getHost().toLowerCase();

        Document doc = new Document();

        doc.add(new StringField(MessageFields.URL_NUM, URL_NUM_FORMAT.format(msg.getNum()), Store.YES));
        doc.add(new StringField(MessageFields.TOPIC_CODE, Integer.toString(msg.getTopicCode()), Store.NO));
        doc.add(new TextField(MessageFields.TITLE, msg.getCleanTitle(), Store.NO)); // "чистый" - индексируем, не храним
        doc.add(new StringField(MessageFields.NICK, msg.getNick(), Store.NO));
        doc.add(new StringField(MessageFields.REG, msg.isReg() ? TRUE : FALSE, Store.NO));

        doc.add(new StringField(MessageFields.HOST, hostLowerCase, Store.NO));
        doc.add(new StringField(MessageFields.HOST_REVERSED, StringUtils.reverse(hostLowerCase), Store.NO));

        doc.add(new StringField(MessageFields.DATE, DateTools.dateToString(msg.getDate(), DateTools.Resolution.MINUTE), Store.NO));
        doc.add(new TextField(MessageFields.BODY, msg.getCleanBody(), Store.NO)); // "чистый" - индексируем, не храним
        doc.add(new StringField(MessageFields.HAS_URL, MessageLogic.hasUrl(msg) ? TRUE : FALSE, Store.NO));
        doc.add(new StringField(MessageFields.HAS_IMG, MessageLogic.hasImg(msg, forumId) ? TRUE : FALSE, Store.NO));

        doc.add(new StringField(MessageFields.IS_ROOT, msg.getParentNum() <= 0 ? TRUE : FALSE, Store.NO));

        return doc;
    }

    /**
     * for use in indexing daemon
     * indexes end marks msgs in db as indexed
     * indexes [from, to] including...
     * TODO: this must be transactional!
     */
    @Override
    public void index(String forumId, int from, int to) throws IndexerException {
        if (from <= 1) {
            log.info("[!] Dropping index for site: " + forumId + ", as from index=" + from);
            try {
                searchLogic.dropIndex(forumId);
            } catch (IOException e) {
                log.error("Error while dropping index for site: " + forumId, e);
            }
        }

        log.info(String.format(forumId + " - Adding %s msgs [%s-%s] to index...", to - from + 1, from, to));

        addMessagesToIndex(forumId, from, to + 1);

        log.info(forumId + " - Setting last indexed: " + to);

        appLogic.setLastIndexedNumber(forumId, to);
    }
}
