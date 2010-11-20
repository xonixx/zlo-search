package info.xonix.zlo.search.logic.site;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.utils.Check;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

/**
 * Author: gubarkov
 * Date: 01.10.2007
 * Time: 21:18:54
 */
public class MessageRetriever implements InitializingBean {

//    private static final int LIMIT_PER_SECOND = Integer.parseInt(Config.getProp("retriever.limit.per.second"));
    private static Logger log = Logger.getLogger(MessageRetriever.class);

    private Config config;
    private PageParser pageParser;
    private PageRetriever pageRetriever;

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setPageParser(PageParser pageParser) {
        this.pageParser = pageParser;
    }

    public void setPageRetriever(PageRetriever pageRetriever) {
        this.pageRetriever = pageRetriever;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
        Check.isSet(pageParser, "pageParser");
        Check.isSet(pageRetriever, "pageRetriever");
    }

    public Message getMessage(Site site, int num) throws RetrieverException, PageParseException {
        return pageParser.parseMessage(site, pageRetriever.getPageContentByNumber(site, num), num);
    }

    public List<Message> getMessages(Site source, int from, int to) throws RetrieverException, PageParseException {
        Set<Integer> nums = new HashSet<Integer>(to - from);
        for (int i = from; i < to; i++) {
            nums.add(i);
        }
        return getMessages(source, nums);
    }

    public List<Message> getMessages(Site source, Iterable<Integer> numsToSave) throws RetrieverException, PageParseException {
        return getMessages(source, numsToSave, config.getRetrieverThreadNum());
    }

    public List<Message> getMessages(Site site, Iterable<Integer> numsToSave, int threadsNum) throws RetrieverException, PageParseException {
        final List<Message> msgs;

        if (threadsNum == 1) {
            msgs = new ArrayList<Message>();

            for (int num : numsToSave) {
                long t1 = System.currentTimeMillis();

                msgs.add(getMessage(site, num));

                long delta = System.currentTimeMillis() - t1;
                long toSleep = 1000 / site.getIndexerLimitPerSecond() - delta;
                if (toSleep > 0) {
                    try {
                        Thread.sleep(toSleep);
                    } catch (InterruptedException e) {
                        log.warn("This should not happen!", e);
                    }
                }
            }
        } else {
            msgs = new Vector<Message>();
            List<Thread> threads = new ArrayList<Thread>();

            // starting all threads
            for (int i = 0; i < threadsNum; i++) {
                Thread t = new MessageRetrieverThread(this, site, numsToSave, msgs);
                t.start();
                threads.add(t);
            }

            // joining them - main thread waits for all
            try {
                for (Thread t : threads)
                    t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Exception exc = MessageRetrieverThread.getException();
            if (exc != null) {
                throw new RetrieverException("Page retrieve problem with " + site.getName(), exc);
            }
        }

        Collections.sort(msgs, Message.NUM_COMPARATOR);

        return msgs;
    }

    public int getLastMessageNumber(Site site) throws RetrieverException {
        return pageRetriever.getLastRootMessageNumber(site);
    }
}
