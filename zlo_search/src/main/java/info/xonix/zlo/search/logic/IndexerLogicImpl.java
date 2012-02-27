package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.doubleindex.DoubleIndexManager;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.factory.SiteFactory;
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

    private SiteFactory<IndexWriter> siteToIndexWriter = new SiteFactory<IndexWriter>() {
        @Override
        protected IndexWriter create(Site site) {
            IndexWriter writer;
            try {
                IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
                        LuceneVersion.VERSION, config.getMessageAnalyzer());

                writer = new IndexWriter(FSDirectory.open(getIndexDir(site)),indexWriterConfig);

                writer.setMergeFactor(7); // optimized for search : TODO
            } catch (IOException e) {
                throw new RuntimeException("Can't create writer", e);
            }
            return writer;
        }

        @Override
        protected void close(Site site, IndexWriter indexWriter) {
            log.info("Closing indexWriter for site: " + site.getName());
            try {
                indexWriter.close();
            } catch (IOException e) {
                log.error("Problem closing indexWriter for site: " + site.getName(), e);
            }
        }

        private File getIndexDir(Site site) {
            return new File(config.getIndexDirDouble(site) + "/" + DoubleIndexManager.SMALL_INDEX_DIR);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
        Check.isSet(appLogic, "appLogic");
        Check.isSet(searchLogic, "searchLogic");
    }

    private IndexWriter getWriter(Site site) {
        return siteToIndexWriter.get(site);
    }

    private void addMessagesToIndex(Site site, int start, int end) throws IndexerException {
        final IndexWriter writer = getWriter(site);
        try {
            for (Message msg : appLogic.getMessages(site, start, end)) {
                if (msg.isOk()) {
                    log.debug(site.getName() + " - Addind: " + (config.isDebug() ? msg : msg.getNum()));

                    writer.addDocument(messageToDocument(site, msg));
                } else {
                    log.debug(site.getName() + " - Not adding: " + msg.getNum() + " with status: " + msg.getStatus());
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

    private Document messageToDocument(Site site, @Nonnull Message msg) {
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
            doc.add(new Field(MessageFields.HAS_URL, msg.isHasUrl() ? TRUE : FALSE, Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(MessageFields.HAS_IMG, msg.isHasImg(site) ? TRUE : FALSE, Field.Store.NO, Field.Index.NOT_ANALYZED));

            return doc;
    }

    /**
     * for use in indexing daemon
     * indexes end marks msgs in db as indexed
     * indexes [from, to] including...
     * TODO: this must be transactional!
     */
    @Override
    public void index(Site site, int from, int to) throws IndexerException {
        final String siteName = site.getName();

        if (from <= 1) {
            log.info("[!] Dropping index for site: " + siteName + ", as from index=" + from);
            try {
                searchLogic.dropIndex(site);
            } catch (IOException e) {
                log.error("Error while dropping index for site: " + siteName, e);
            }
        }

        log.info(String.format(siteName + " - Adding %s msgs [%s-%s] to index...", to - from + 1, from, to));

        addMessagesToIndex(site, from, to + 1);

        log.info(siteName + " - Setting last indexed: " + to);

        appLogic.setLastIndexedNumber(site, to);
    }

    @Override
    public void closeIndexWriter(Site site) {
        siteToIndexWriter.reset(site);
    }

    @Override
    public void closeIndexWriters() {
        siteToIndexWriter.reset();
    }
}
