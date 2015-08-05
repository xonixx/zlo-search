package info.xonix.zlo.search.ignoressl;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * User: xonix
 * Date: 05.08.15
 * Time: 17:03
 *
 * We may need this on obsolete Java installations (with obsolete certificates)
 */
public class HttpsCertVerificationDisabler {
    public HttpsCertVerificationDisabler() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = getIgnoringSslContext();

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        // **********************
        // for commons-httpclient
        // **********************
        Protocol.registerProtocol("https",
                new Protocol("https", new IgnoreSSLProtocolSocketFactory(), 443));
    }

    static SSLContext getIgnoringSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        return sc;
    }

    /**
     * <p>
     * EasySSLProtocolSocketFactory can be used to creats SSL {@link Socket}s
     * that accept self-signed certificates.
     * </p>
     * <p>
     * This socket factory SHOULD NOT be used for productive systems
     * due to security reasons, unless it is a concious decision and
     * you are perfectly aware of security implications of accepting
     * self-signed certificates
     * </p>
     *
     * <p>
     * Example of using custom protocol socket factory for a specific host:
     *     <pre>
     *     Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
     *
     *     URI uri = new URI("https://localhost/", true);
     *     // use relative url only
     *     GetMethod httpget = new GetMethod(uri.getPathQuery());
     *     HostConfiguration hc = new HostConfiguration();
     *     hc.setHost(uri.getHost(), uri.getPort(), easyhttps);
     *     HttpClient client = new HttpClient();
     *     client.executeMethod(hc, httpget);
     *     </pre>
     * </p>
     * <p>
     * Example of using custom protocol socket factory per default instead of the standard one:
     *     <pre>
     *     Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
     *     Protocol.registerProtocol("https", easyhttps);
     *
     *     HttpClient client = new HttpClient();
     *     GetMethod httpget = new GetMethod("https://localhost/");
     *     client.executeMethod(httpget);
     *     </pre>
     * </p>
     *
     * @author <a href="mailto:oleg -at- ural.ru">Oleg Kalnichevski</a>
     *
     * <p>
     * DISCLAIMER: HttpClient developers DO NOT actively support this component.
     * The component is provided as a reference material, which may be inappropriate
     * for use without additional customization.
     * </p>
     */
    public static class IgnoreSSLProtocolSocketFactory implements SecureProtocolSocketFactory {

        /** Log object for this class. */
        private static final Log LOG = LogFactory.getLog(IgnoreSSLProtocolSocketFactory.class);

        private SSLContext sslcontext = null;

        /**
         * Constructor for EasySSLProtocolSocketFactory.
         */
        public IgnoreSSLProtocolSocketFactory() {
            super();
        }

        private static SSLContext createEasySSLContext() {
            try {
                return getIgnoringSslContext();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new HttpClientError(e.toString());
            }
        }

        private SSLContext getSSLContext() {
            if (this.sslcontext == null) {
                this.sslcontext = createEasySSLContext();
            }
            return this.sslcontext;
        }

        /**
         * @see SecureProtocolSocketFactory#createSocket(String,int,java.net.InetAddress,int)
         */
        public Socket createSocket(
                String host,
                int port,
                InetAddress clientHost,
                int clientPort)
                throws IOException, UnknownHostException {

            return getSSLContext().getSocketFactory().createSocket(
                    host,
                    port,
                    clientHost,
                    clientPort
            );
        }

        /**
         * Attempts to get a new socket connection to the given host within the given time limit.
         * <p>
         * To circumvent the limitations of older JREs that do not support connect timeout a
         * controller thread is executed. The controller thread attempts to create a new socket
         * within the given limit of time. If socket constructor does not return until the
         * timeout expires, the controller terminates and throws an {@link ConnectTimeoutException}
         * </p>
         *
         * @param host the host name/IP
         * @param port the port on the host
         * @param clientHost the local host name/IP to bind the socket to
         * @param clientPort the port on the local machine
         * @param params {@link HttpConnectionParams Http connection parameters}
         *
         * @return Socket a new socket
         *
         * @throws IOException if an I/O error occurs while creating the socket
         * @throws UnknownHostException if the IP address of the host cannot be
         * determined
         */
        public Socket createSocket(
                final String host,
                final int port,
                final InetAddress localAddress,
                final int localPort,
                final HttpConnectionParams params
        ) throws IOException, UnknownHostException, ConnectTimeoutException {
            if (params == null) {
                throw new IllegalArgumentException("Parameters may not be null");
            }
            int timeout = params.getConnectionTimeout();
            SocketFactory socketfactory = getSSLContext().getSocketFactory();
            if (timeout == 0) {
                return socketfactory.createSocket(host, port, localAddress, localPort);
            } else {
                Socket socket = socketfactory.createSocket();
                SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
                SocketAddress remoteaddr = new InetSocketAddress(host, port);
                socket.bind(localaddr);
                socket.connect(remoteaddr, timeout);
                return socket;
            }
        }

        /**
         * @see SecureProtocolSocketFactory#createSocket(String,int)
         */
        public Socket createSocket(String host, int port)
                throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(
                    host,
                    port
            );
        }

        /**
         * @see SecureProtocolSocketFactory#createSocket(Socket,String,int,boolean)
         */
        public Socket createSocket(
                Socket socket,
                String host,
                int port,
                boolean autoClose)
                throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(
                    socket,
                    host,
                    port,
                    autoClose
            );
        }

        public boolean equals(Object obj) {
            return ((obj != null) && obj.getClass().equals(IgnoreSSLProtocolSocketFactory.class));
        }

        public int hashCode() {
            return IgnoreSSLProtocolSocketFactory.class.hashCode();
        }
    }
}
