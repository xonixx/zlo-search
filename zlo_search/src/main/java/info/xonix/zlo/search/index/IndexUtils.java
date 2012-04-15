package info.xonix.zlo.search.index;

import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.index.doubleindex.DoubleIndexManager;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * User: gubarkov
 * Date: 15.04.12
 * Time: 22:29
 */
public class IndexUtils {
    private final static Logger log = Logger.getLogger(IndexUtils.class);

    public static void createEmptyIndex(File path) throws IOException {
        // TODO: test
        final IndexWriter indexWriter = new IndexWriter(dir(path),
                new IndexWriterConfig(LuceneVersion.VERSION, null)
                        .setOpenMode(IndexWriterConfig.OpenMode.CREATE));
//                null, true, IndexWriter.MaxFieldLength.UNLIMITED);
        indexWriter.close();
    }

    public static Directory dir(File dir) throws IOException {
        if (dir == null) {
            throw new NullPointerException("dir");
        }

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }

        return FSDirectory.open(dir);
    }

    public static void clean(IndexReader ir) {
        try {
            if (ir != null) {
                ir.close();
            }
        } catch (IOException e) {
            log.error("Error while closing index reader (ignored)", e);
        }
    }
}
