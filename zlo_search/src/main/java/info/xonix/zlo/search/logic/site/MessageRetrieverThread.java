package info.xonix.zlo.search.logic.site;

import info.xonix.zlo.search.domain.Message;
import info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf.WwwconfParams;
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

    private String forumId;
    private WwwconfParams wwwconfParams;

    private MessageRetriever messageRetriever;

    private static Exception exception = null;

    private static Iterator<Integer> numsIterator = null;

    public MessageRetrieverThread(MessageRetriever messageRetriever,
                                  String forumId, WwwconfParams wwwconfParams, Iterable<Integer> numsToSave, List<Message> msgs) {
        super("MessageRetriever #" + threadNum);

        this.messageRetriever = messageRetriever;
        this.forumId = forumId;

        if (threadNum == 0 && exception != null) {
            // clean the exception of last range retrieve
            exception = null;
        }

        this.wwwconfParams = wwwconfParams;

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
                    myMsgs.add(messageRetriever.getMessage(forumId, wwwconfParams, nextNum));
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
