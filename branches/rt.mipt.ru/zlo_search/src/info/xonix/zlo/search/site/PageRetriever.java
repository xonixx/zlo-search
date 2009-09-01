package info.xonix.zlo.search.site;

import info.xonix.zlo.search.config.Config;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 17:36:44
 */
public class PageRetriever {
    private static final Logger logger = Logger.getLogger(PageRetriever.class);

    public static final int THREADS_NUMBER = Integer.parseInt(Config.getProp("retriever.threads"));

    private static HttpClient HTTP_CLIENT;

    private SiteAccessor siteAccessor;
    private Pattern INDEX_UNREG_RE;

    static {
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        // 100 - so many, because we will manage concurrancy ourselves
        connectionManager.getParams().setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, 100);
        HTTP_CLIENT = new HttpClient(connectionManager);
        if (Config.USE_PROXY) {
            HTTP_CLIENT.getHostConfiguration().setProxy(Config.PROXY_HOST, Config.PROXY_PORT);
        }
    }

    public PageRetriever(SiteAccessor siteAccessor) {
        this.siteAccessor = siteAccessor;
        INDEX_UNREG_RE = Pattern.compile(siteAccessor.getLINK_INDEX_REGEX());
    }

    public String getPageContentByNumber(int num) throws IOException {
        GetMethod getMethod = formGetMethod("http://" + siteAccessor.getSITE_URL() + siteAccessor.getREAD_QUERY() + num);

        List<String> stringGroups = new ArrayList<String>();
        InputStream is = null;
        int totalRead = 0;

        try {
            HTTP_CLIENT.executeMethod(getMethod);

            // реализовано чтение до "<BIG>Сообщения в этом потоке</BIG>"
            is = getMethod.getResponseBodyAsStream();
            stringGroups.add("");

            int currSize, lenRead;
            String ending;
            byte[] buff = new byte[Config.BUFFER];

            do {
                lenRead = is.read(buff);
                /*
                if (lenRead != buff.length) {
                    logger.warn("buff.length=" + buff.length + " but lenRead=" + lenRead); // <-- 4, 120
                }
                */
                if (lenRead <= 0) {
                    logger.warn("lenRead = " + lenRead + " while receiving " + num + ". It possibly means that message can't be parsed correctly...");
                    break;
                }

                totalRead += lenRead;

                stringGroups.add(new String(buff, 0, lenRead, Config.CHARSET_NAME));
                currSize = stringGroups.size();
                ending = stringGroups.get(currSize - 2) + stringGroups.get(currSize - 1);
            } while (
                    ending.indexOf(siteAccessor.getMARK_END_MSG_1()) == -1 &&
                            ending.indexOf(siteAccessor.getMARK_END_MSG_2()) == -1 && // if user have sign - won't read it all
                            ending.indexOf(siteAccessor.getMSG_NOT_EXIST_OR_WRONG()) == -1
                    );

            // read till end - seems that closing while not end riached causes board crash
            /* let's save traffic
            for (lenRead = 0; lenRead > 0;) {
                lenRead = is.read(buff);
            }
            */

        } finally {
            if (is != null)
                is.close();
            getMethod.releaseConnection(); // http://jakarta.apache.org/httpcomponents/httpclient-3.x/threading.html
        }
        StringBuffer sb = new StringBuffer(totalRead);
        for (String s : stringGroups) {
            sb.append(s);
        }
        stringGroups.clear();

        return sb.toString();
    }

    /* load page until first root-message found
    *  returns last number of root-message or -1 if not found
     */
    public int getLastRootMessageNumber() throws IOException {
        GetMethod getMethod = formGetMethod("http://" + siteAccessor.getSITE_URL());

        InputStream is = null;
        Matcher m = null;

        try {
            HTTP_CLIENT.executeMethod(getMethod);
            // реализовано чтение до матча с PageParser.INDEX_UNREG_RE"
            is = getMethod.getResponseBodyAsStream();
            List<String> stringGroups = new ArrayList<String>();
            stringGroups.add("");

            int currSize, lenRead;
            byte[] buff = new byte[Config.BUFFER];
            do {
                lenRead = is.read(buff);
                if (lenRead <= 0) {
                    logger.warn("lenRead = " + lenRead);
                    throw new IOException("lenRead = " + lenRead);
                }
                stringGroups.add(new String(buff, 0, lenRead, Config.CHARSET_NAME));
                currSize = stringGroups.size();
                m = INDEX_UNREG_RE.matcher(stringGroups.get(currSize - 2) + stringGroups.get(currSize - 1));
            } while (!m.find());

            // read till end
            for (lenRead = 0; lenRead > 0;) {
                lenRead = is.read(buff);
            }

        } finally {
            if (is != null)
                is.close();
            getMethod.releaseConnection();
        }

        return Integer.parseInt(m.group(1));
    }

    private GetMethod formGetMethod(String uri) {
        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Host", siteAccessor.getSITE_URL());
        getMethod.addRequestHeader("User-Agent", Config.USER_AGENT);
        getMethod.getParams().setVersion(HttpVersion.HTTP_1_0); // to prevent chunk transfer-encoding in reply
        return getMethod;
    }
}
