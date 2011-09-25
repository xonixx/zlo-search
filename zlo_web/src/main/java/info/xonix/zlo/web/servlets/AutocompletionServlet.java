package info.xonix.zlo.web.servlets;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * User: gubarkov
 * Date: 26.09.11
 * Time: 0:10
 */
public class AutocompletionServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AutocompletionServlet.class);

    private final AppLogic appLogic = AppSpringContext.get(AppLogic.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final List<String> strings = appLogic.autoCompleteText(Site.forName("zlo"), "test", 10);

        JSONArray list = new JSONArray();

        for (String string : strings) {
            JSONObject line = new JSONObject();

            line.put("value", string);

            list.add(line);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");

        final OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream());
        list.writeJSONString(writer);

        writer.flush();
    }
}
