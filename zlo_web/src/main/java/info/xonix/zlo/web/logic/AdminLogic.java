package info.xonix.zlo.web.logic;

import info.xonix.zlo.search.daemons.DaemonLauncher;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.spring.AppSpringContext;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: gubarkov
 * Date: 24.03.12
 * Time: 14:54
 */
public class AdminLogic {
    private static AppLogic appLogic = AppSpringContext.get(AppLogic.class);

    public static final float MEGABYTE = 1024 * 1024f;

    public static Map<String, Float> memoryReport() {
        final Runtime runtime = Runtime.getRuntime();

        Map<String, Float> memory = new LinkedHashMap<String, Float>();
        memory.put("Free", runtime.freeMemory() / MEGABYTE);
        memory.put("Total", runtime.totalMemory() / MEGABYTE);
        memory.put("Max", runtime.maxMemory() / MEGABYTE);

        return memory;
    }

    public static Map<String, String> versionsReport(ServletContext application) {
        Map<String ,String > versions = new LinkedHashMap<String, String>();

        versions.put("Server Info", application.getServerInfo());
        versions.put("Servlet Engine Version", "" + application.getMajorVersion() +  application.getMinorVersion());
        versions.put("JSP Version", JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion());
        versions.put("Java Version", System.getProperty("java.version"));
        versions.put("Java VM Version", System.getProperty("java.vm.version"));

        return versions;
    }

    public static void reindex(String forumId) {
        DaemonLauncher.shutdownAll();

        appLogic.setLastIndexedNumber(forumId, -1);

        DaemonLauncher.startAllActive();
    }
}
