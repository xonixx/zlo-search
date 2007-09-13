package org.xonix.zlo.search;

import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

/**
 * Author: gubarkov
 * Date: 13.09.2007
 * Time: 16:40:04
 */

// data access object
public class DAO {
    public static class Site {
        public static ZloMessage getMessageByNumber(int num) throws IOException {
            return PageParser.parseMessage(PageRetriever.getPageContentByNumber(num), num);
        }

        public static List<ZloMessage> getMessages(int from, int to, int threads) throws IOException {
            List<ZloMessage> msgs;
            if (threads == 1) {
                msgs = new ArrayList<ZloMessage>();

                for (int i = 0; i <= to; i++) {
                    msgs.add(getMessageByNumber(i));
                }
            } else {
                msgs = new Vector<ZloMessage>();
                // todo: implement multithreaded
            }
            return msgs;
        }

        public static List<ZloMessage> getMessages(int from, int to) throws IOException {
            return getMessages(from, to, 1);
        }

        public static int getLastRootMessageNumber() throws IOException {
            return PageRetriever.getLastRootMessageNumber();
        }
    }

    public static class DB {

    }
}
