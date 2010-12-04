package info.xonix.zlo.search.logic.site;

import info.xonix.zlo.search.HttpHeader;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.utils.Check;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 17:36:44
 */
public class PageRetriever implements InitializingBean {
    private static final Logger log = Logger.getLogger(PageRetriever.class);

    private HttpClient httpClient;

    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");

        init();
    }

    private void init() {
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        // 100 - so many, because we will manage concurrancy ourselves
        connectionManager.getParams().setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, 100);
        httpClient = new HttpClient(connectionManager);
        if (config.isUseProxy()) {
            httpClient.getHostConfiguration().setProxy(
                    config.getProxyHost(),
                    config.getProxyPort());
        }
    }

    public String getPageContentByNumber(Site site, int num) throws RetrieverException {
        GetMethod getMethod = formGetMethod(site, "http://" + site.getSiteUrl() + site.getReadQuery() + num);

        final List<String> stringGroups = new ArrayList<String>();
        InputStream is = null;
        int totalRead = 0;

        try {
            is = getInputStream(getMethod);

            // реализовано чтение до "<BIG>Сообщения в этом потоке</BIG>"
            stringGroups.add("");

            int currSize, lenRead;
            String ending;
            byte[] buff = new byte[config.getBuffer()];

            do {
                lenRead = is.read(buff);
                /*
                if (lenRead != buff.length) {
                    log.warn("buff.length=" + buff.length + " but lenRead=" + lenRead); // <-- 4, 120
                }
                */
                if (lenRead <= 0) {
                    log.warn("lenRead = " + lenRead + " while receiving " + num + ". It possibly means that message can't be parsed correctly...");
                    break;
                }

                totalRead += lenRead;

                stringGroups.add(new String(buff, 0, lenRead, config.getCharsetName()));
                currSize = stringGroups.size();
                ending = stringGroups.get(currSize - 2) + stringGroups.get(currSize - 1);
            } while (
                    ending.indexOf(site.getMarkEndMsg1()) == -1 &&
                            ending.indexOf(site.getMarkEndMsg2()) == -1 && // if user have sign - won't read it all
                            ending.indexOf(site.getMsgNotExistOrWrong()) == -1
                    );

            // read till end - seems that closing while not end reached causes board crash
            /* let's save traffic
            for (lenRead = 0; lenRead > 0;) {
                lenRead = is.read(buff);
            }
            */

        } catch (UnsupportedEncodingException e) {
            throw new RetrieverException(e);
        } catch (IOException e) {
            throw new RetrieverException("Error while reading response body", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.warn("Exception while close.. hmm", e);
                }
            }
            getMethod.releaseConnection(); // http://jakarta.apache.org/httpcomponents/httpclient-3.x/threading.html
        }
        StringBuffer sb = new StringBuffer(totalRead);
        for (String s : stringGroups) {
            sb.append(s);
        }
        stringGroups.clear();

        return sb.toString();
    }

    /**
     * load page until first root-message found
     * returns last number of root-message or -1 if not found
     *
     * @param site site
     * @return number of last msg
     * @throws RetrieverException on i/o exception
     */
    public int getLastRootMessageNumber(Site site) throws RetrieverException {
        GetMethod getMethod = formGetMethod(site, "http://" + site.getSiteUrl());

        InputStream is = null;
        Matcher m = null;

        try {
            is = getInputStream(getMethod);
            // реализовано чтение до матча с site.getLinkIndexRe()
            List<String> stringGroups = new ArrayList<String>();
            stringGroups.add("");

            int currSize, lenRead;
            byte[] buff = new byte[config.getBuffer()];
            do {
                lenRead = is.read(buff);
                if (lenRead <= 0) {
                    log.warn("lenRead = " + lenRead);
                    throw new RetrieverException("lenRead = " + lenRead);
                }
                stringGroups.add(new String(buff, 0, lenRead, config.getCharsetName()));
                currSize = stringGroups.size();
                m = site.getLinkIndexRe().matcher(stringGroups.get(currSize - 2) + stringGroups.get(currSize - 1));
            } while (!m.find());

            // TODO: maybe save traffic?
            // read till end
            for (lenRead = 0; lenRead > 0;) {
                lenRead = is.read(buff);
            }

        } catch (UnsupportedEncodingException e) {
            throw new RetrieverException(e);
        } catch (IOException e) {
            throw new RetrieverException("Error while reading response body", e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    log.warn("Exception while close.. hmm", e);
                }
            getMethod.releaseConnection();
        }

        return Integer.parseInt(m.group(1));
    }

    private InputStream getInputStream(GetMethod getMethod) throws RetrieverException {
        try {
            httpClient.executeMethod(getMethod);
        } catch (IOException e) {
            throw new RetrieverException("Error while executing http request", e);
        }

        try {
            return getMethod.getResponseBodyAsStream();
        } catch (IOException e) {
            throw new RetrieverException("Error while fetching response body", e);
        }
    }

    private GetMethod formGetMethod(Site site, String uri) {
        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader(HttpHeader.HOST, site.getSiteUrl());
        getMethod.addRequestHeader(HttpHeader.USER_AGENT, config.getUserAgent());
        getMethod.getParams().setVersion(HttpVersion.HTTP_1_0); // to prevent chunk transfer-encoding in reply
        return getMethod;
    }
}
