package org.xonix.zlo.search;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xonix.zlo.search.config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 17:36:44
 */
public class PageRetriever {
    private static final Logger logger = Logger.getLogger(PageRetriever.class.getName());

    public static final String END_MSG_MARK = "<BIG>Сообщения в этом потоке</BIG>";
    public static final String END_MSG_MARK_SIGN = "<div class=\"sign\">";

    private static HttpClient HTTP_CLIENT = new HttpClient(new MultiThreadedHttpConnectionManager());

    public static String getPageContentByNumber(int num) throws IOException{
        GetMethod getMethod = formGetMethod("http://" + Config.INDEXING_URL + Config.READ_QUERY + num);

        List<String> stringGroups = new ArrayList<String>();
        InputStream is = null;

        try {
            HTTP_CLIENT.executeMethod(getMethod);

            // реализовано чтение до "<BIG>Сообщения в этом потоке</BIG>"
            is = getMethod.getResponseBodyAsStream();
            stringGroups.add("");

            int currSize;
            String ending;
            do {
                byte[] buff = new byte[Config.BUFFER];
                int lenRead = is.read(buff);

                if (lenRead == -1) {
                    logger.warning("lenRead = -1");
                    throw new IOException("lenRead = -1");
//                    break;
                }

                stringGroups.add(new String(buff, 0, lenRead, Config.CHARSET_NAME));
                currSize = stringGroups.size();
                ending = stringGroups.get(currSize - 2) + stringGroups.get(currSize - 1);
            } while(
                ending.indexOf(END_MSG_MARK) == -1 &&
                ending.indexOf(END_MSG_MARK_SIGN) == -1 && // if user have sign - won't read it all
                ending.indexOf(PageParser.MSG_NOT_EXIST_OR_WRONG) == -1
                );
        } finally {
            if (is != null)
                is.close();
            getMethod.releaseConnection(); // http://jakarta.apache.org/httpcomponents/httpclient-3.x/threading.html
        }
        StringBuffer sb = new StringBuffer(stringGroups.size());
        for (String s : stringGroups) {
            sb.append(s);
        }
        return sb.toString();
    }

    /* load page until first root-message found
    *  returns last number of root-message or -1 if not found
     */
    public static int getLastRootMessageNumber() throws IOException {
        GetMethod getMethod = formGetMethod("http://" + Config.INDEXING_URL);

        InputStream is = null;
        Matcher m = null;

        try {
            HTTP_CLIENT.executeMethod(getMethod);
            // реализовано чтение до матча с PageParser.INDEX_UNREG_RE"
            is = getMethod.getResponseBodyAsStream();
            List<String> stringGroups = new ArrayList<String>();
            stringGroups.add("");

            int currSize;
            do {
                byte[] buff = new byte[Config.BUFFER];
                int lenRead = is.read(buff);
                if (lenRead == -1) {
                    logger.warning("lenRead = -1");
                    throw new IOException("lenRead = -1");
//                    break;
                }
                stringGroups.add(new String(buff, 0, lenRead, Config.CHARSET_NAME));
                currSize = stringGroups.size();
                m = PageParser.INDEX_UNREG_RE.matcher(stringGroups.get(currSize - 2) + stringGroups.get(currSize - 1));
            } while(!m.find());
        } finally {
            if (is != null)
                is.close();
            getMethod.releaseConnection();
        }

        return Integer.parseInt(m.group(1));
    }

    private static GetMethod formGetMethod(String uri) {
        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Host", Config.INDEXING_URL);
        getMethod.addRequestHeader("User-Agent", Config.USER_AGENT);
        return getMethod;
    }

    public static void main(String[] args) {
        try {
            String pageS = getPageContentByNumber(3982207);
            System.out.println(pageS);
            System.out.println(PageParser.parseMessage(pageS));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
