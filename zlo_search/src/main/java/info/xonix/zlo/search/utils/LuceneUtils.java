package info.xonix.zlo.search.utils;

import info.xonix.zlo.search.LuceneVersion;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * User: gubarkov
 * Date: 15.04.12
 * Time: 22:29
 */
public class LuceneUtils {
    private final static Logger log = Logger.getLogger(LuceneUtils.class);

    public static void createEmptyIndex(File path) throws IOException {
        final IndexWriter indexWriter = new IndexWriter(dir(path),
                new IndexWriterConfig(LuceneVersion.VERSION, null)
                        .setOpenMode(IndexWriterConfig.OpenMode.CREATE));
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

    public static void close(IndexReader ir) {
        try {
            if (ir != null) {
                ir.close();
            }
        } catch (IOException e) {
            log.error("Error while closing index reader (ignored)", e);
        }
    }

    public static long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            size += file.length();
        }
        return size;
    }

    public static List<String> tokenize(Analyzer analyzer, String fieldName, String text) {
        try {
            final TokenStream tokenStream = analyzer.tokenStream(fieldName, text);

            tokenStream.reset();

            final CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);

            List<String> tokens = new LinkedList<String>();

            while (tokenStream.incrementToken()) {
                final String term = new String(termAttribute.buffer(), 0, termAttribute.length());
                tokens.add(term);
                //System.out.println(">>" + term);
            }
            tokenStream.close();
            return tokens;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
