package info.xonix.zlo.search.logic.site;

import info.xonix.utils.Check;
import info.xonix.zlo.search.HttpHeader;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf.WwwconfParams;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
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

    @Autowired
    private Config config;

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

    public String getPageContentByNumber(WwwconfParams wwwconfParams, int num) throws RetrieverException {
        return getPageText("http://" + wwwconfParams.getSiteUrl() + wwwconfParams.getReadQuery() + num, wwwconfParams);
    }

    /**
     * loads forum root page and parses max msg id
     *
     * @param wwwconfParams wwwconfParams
     * @return number of last msg
     * @throws RetrieverException on i/o exception
     */
    public int getLastRootMessageNumber(WwwconfParams wwwconfParams) throws RetrieverException {
        String rootPageText = getPageText("http://" + wwwconfParams.getSiteUrl(), wwwconfParams);

        Matcher msgLinkMatcher = wwwconfParams.getLinkIndexRe().matcher(rootPageText);
        List<Integer> ids = new LinkedList<Integer>();

        while(msgLinkMatcher.find()) {
            ids.add(Integer.parseInt(msgLinkMatcher.group(1)));
        }

        if (ids.isEmpty()) {
            log.warn("Unable to determine last message num for: " + wwwconfParams.getSiteUrl());
            return -1;
        }

        return Collections.max(ids);
    }

    private String getPageText(String url, WwwconfParams wwwconfParams) throws RetrieverException {
        GetMethod getMethod = formGetMethod(wwwconfParams, url);

        try {
            httpClient.executeMethod(getMethod);
        } catch (IOException e) {
            throw new RetrieverException("Error while executing http request: " + url, e);
        }

        InputStream is = null;
        try {
            is = getMethod.getResponseBodyAsStream();
            return IOUtils.toString(is, wwwconfParams.getSiteCharset());
        } catch (IOException e) {
            throw new RetrieverException("Error while fetching response body: " + url, e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    log.warn("Exception while close.. hmm", e);
                }
            getMethod.releaseConnection();
        }
    }

    private GetMethod formGetMethod(WwwconfParams wwwconfParams, String uri) {
        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader(HttpHeader.HOST, wwwconfParams.getSiteUrl());
        getMethod.addRequestHeader(HttpHeader.USER_AGENT, config.getUserAgent());
        getMethod.getParams().setVersion(HttpVersion.HTTP_1_0); // to prevent chunk transfer-encoding in reply
        return getMethod;
    }
}
