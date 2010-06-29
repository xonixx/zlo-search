package info.xonix.zlo.search.logic.site;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.Site;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * todo: not tested
 */
class MessageRetrieverThread extends Thread {
    private final static Logger log = Logger.getLogger(MessageRetrieverThread.class);
    private static int threadNum = 0;

    private List<Message> msgs;
    private List<Message> myMsgs = new ArrayList<Message>();
    private Site site;

    private MessageRetriever messageRetriever;

    private static Exception exception = null;

    private static Iterator<Integer> numsIterator = null;

    public MessageRetrieverThread(MessageRetriever messageRetriever, Site site, Iterable<Integer> numsToSave, List<Message> msgs) {
        super("MessageRetriever #" + threadNum);

        this.messageRetriever = messageRetriever;

        if (threadNum == 0 && exception != null) {
            // clean the exception of last range retrieve
            exception = null;
        }

        this.site = site;

        if (numsIterator == null)
            numsIterator = numsToSave.iterator();

        this.msgs = msgs;
        log.info("Born #" + threadNum);
        threadNum++;
    }

    private synchronized int getNextNum() {
        if (numsIterator.hasNext() && exception == null)
            return numsIterator.next();
        else
            return -1;
    }

    public void run() {
        try {
            int nextNum;
            while ((nextNum = getNextNum()) != -1) {
                try {
                    log.debug("Downloading: " + nextNum + " by " + getName());
                    myMsgs.add(messageRetriever.getMessage(site, nextNum));
                } catch (Exception e) {
                    exception = e;
                    log.warn(getName() + " exiting because of exception: " + e);
                }
            }
        } finally {
            msgs.addAll(myMsgs);
            log.info(getName() + " finished, downloaded: " + myMsgs.size());
            threadNum--;

            if (threadNum == 0) {
                // no more to download - making MessageRetriever ready for next requests
                numsIterator = null;
            }
        }
    }

    public static Exception getException() {
        return exception;
    }
}
