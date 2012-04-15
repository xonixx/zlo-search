package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.index.IndexManager;
import info.xonix.zlo.search.index.IndexUtils;
import info.xonix.zlo.search.model.Message;
import info.xonix.utils.Check;
import info.xonix.utils.factory.StringFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.io.File;
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

    private StringFactory<IndexWriter> siteToIndexWriter = new StringFactory<IndexWriter>() {
        @Override
        protected IndexWriter create(String forumId) {
            IndexWriter writer;
            try {

                  writer = IndexManager.get(forumId).createWriter();
//                VVV-- NOT works in Lucene 3.6
//                writer.setMergeFactor(7); // optimized for search : TODO
            } catch (IOException e) {
                throw new RuntimeException("Can't create writer", e);
            }
            return writer;
        }

        @Override
        protected void close(String forumId, IndexWriter indexWriter) {
            log.info("Closing indexWriter for site: " + forumId);
            try {
                indexWriter.close();
            } catch (IOException e) {
                log.error("Problem closing indexWriter for site: " + forumId, e);
            }
        }

    };

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
        Check.isSet(appLogic, "appLogic");
        Check.isSet(searchLogic, "searchLogic");
    }

    private IndexWriter getWriter(String forumId) {
        return siteToIndexWriter.get(forumId);
    }

    private void addMessagesToIndex(String forumId, int start, int end) throws IndexerException {
        final IndexWriter writer = getWriter(forumId);
        try {
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

        doc.add(new Field(MessageFields.URL_NUM, URL_NUM_FORMAT.format(msg.getNum()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.TOPIC_CODE, Integer.toString(msg.getTopicCode()), Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.TITLE, msg.getCleanTitle(), Field.Store.NO, Field.Index.ANALYZED)); // "чистый" - индексируем, не храним
        doc.add(new Field(MessageFields.NICK, msg.getNick().toLowerCase(), Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.REG, msg.isReg() ? TRUE : FALSE, Field.Store.NO, Field.Index.NOT_ANALYZED));

        doc.add(new Field(MessageFields.HOST, hostLowerCase, Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.HOST_REVERSED, StringUtils.reverse(hostLowerCase), Field.Store.NO, Field.Index.NOT_ANALYZED));

        doc.add(new Field(MessageFields.DATE, DateTools.dateToString(msg.getDate(), DateTools.Resolution.MINUTE), Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.BODY, msg.getCleanBody(), Field.Store.NO, Field.Index.ANALYZED)); // "чистый" - индексируем, не храним
        doc.add(new Field(MessageFields.HAS_URL, MessageLogic.hasUrl(msg) ? TRUE : FALSE, Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field(MessageFields.HAS_IMG, MessageLogic.hasImg(msg, forumId) ? TRUE : FALSE, Field.Store.NO, Field.Index.NOT_ANALYZED));

        doc.add(new Field(MessageFields.IS_ROOT, msg.getParentNum() <= 0 ? TRUE : FALSE, Field.Store.NO, Field.Index.NOT_ANALYZED));

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

    @Override
    public void closeIndexWriter(String forumId) {
        siteToIndexWriter.reset(forumId);
    }

    @Override
    public void closeIndexWriters() {
        siteToIndexWriter.reset();
    }
}
