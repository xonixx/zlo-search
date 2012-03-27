package info.xonix.launcher;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import sun.misc.JarFilter;

import java.io.File;
import java.io.IOException;

/**
 * User: gubarkov
 * Date: 04.03.12
 * Time: 16:19
 */
public class LaunchBoardSearch {
    public static void main(String[] args) throws Exception {
        String rootFolder = ".";
        String zloSearch = join(rootFolder, "zlo_search");
        String zloWeb = join(rootFolder, "zlo_web");

        Server server = new Server();

        Connector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);

        WebAppContext root = new WebAppContext(join(zloWeb, "src/main/webapp"), "/");
//        WebAppContext root = new WebAppContext(join(zloWeb, "src/main/webapp"), "/lol");

        WebAppClassLoader rootClassLoader = new WebAppClassLoader(root);
        rootClassLoader.addClassPath(join(zloWeb, "target/classes"));
        rootClassLoader.addClassPath(join(zloSearch, "target/classes"));

        addAllDependentJars(rootClassLoader, zloWeb);

        root.setClassLoader(rootClassLoader);

        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(root);

        server.setHandler(handlerList);

        server.start();
    }

    private static void addAllDependentJars(WebAppClassLoader rootClassLoader, String zloWeb) throws IOException {
        final File libDir = new File(join(zloWeb, "target/zlo_web-0.7/WEB-INF/lib"));

        final File[] jars = libDir.listFiles(new JarFilter());

        for (File jar : jars) {
            if (jar.getName().startsWith("zlo_search")) {
                // don't add this
                continue;
            }
            
            rootClassLoader.addClassPath(jar.getAbsolutePath());
        }
    }

    private static String join(String rootFolder, final String child) {
        return new File(rootFolder, child).getAbsolutePath();
    }
}
