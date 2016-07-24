package info.xonix.launcher;

import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.*;
import sun.misc.JarFilter;

import java.io.File;
import java.io.IOException;

/**
 * User: gubarkov
 * Date: 04.03.12
 * Time: 16:19
 */
public class LaunchBoardSearch {
    static String[] modules = {
            "zlo_search", "zlo_web",
            "xmlfp", "xonix-utils",

            "slf4j" // included from pom!
    };

    public static void main(String[] args) throws Exception {
        String rootFolder = ".";
        String zloWeb = join(rootFolder, "zlo_web");

        Server server = new Server(8080);

        Connector connector = new ServerConnector(server);
        server.addConnector(connector);

        WebAppContext root = new WebAppContext(join(zloWeb, "src/main/webapp"), "/");
//        WebAppContext root = new WebAppContext(join(zloWeb, "src/main/webapp"), "/lol");

        EnvConfiguration envConfiguration = new EnvConfiguration();
        envConfiguration.setJettyEnvXml(LaunchBoardSearch.class.getResource("jetty-env.xml"));
        root.setConfigurations(new Configuration[]{
                new WebInfConfiguration(),// default
                new WebXmlConfiguration(),// default
                envConfiguration
        });

        WebAppClassLoader rootClassLoader = new WebAppClassLoader(root);

        for (String moduleName : modules) {
            rootClassLoader.addClassPath(join(join(rootFolder, moduleName), "target/classes"));
        }

        addAllDependentJars(rootClassLoader, zloWeb);

        root.setClassLoader(rootClassLoader);

        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(root);

        server.setHandler(handlerList);

        server.start();
    }

    private static void addAllDependentJars(WebAppClassLoader rootClassLoader, String zloWeb) throws IOException {
        final File libDir = new File(join(zloWeb, "target/zlo_web-2.0/WEB-INF/lib"));

        final File[] jars = libDir.listFiles(new JarFilter());

        jar_loop:
        for (File jar : jars) {
            for (String moduleName : modules) {
                if (jar.getName().startsWith(moduleName)) {
                    // don't add this
                    continue jar_loop;
                }
            }

            System.out.println("Loading: " + jar.getName());
            rootClassLoader.addClassPath(jar.getAbsolutePath());
        }
    }

    private static String join(String rootFolder, final String child) {
        return new File(rootFolder, child).getAbsolutePath();
    }
}
