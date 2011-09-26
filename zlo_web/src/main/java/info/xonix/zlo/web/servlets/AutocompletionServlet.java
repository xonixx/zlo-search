package info.xonix.zlo.web.servlets;

import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * User: gubarkov
 * Date: 26.09.11
 * Time: 0:10
 */
public class AutocompletionServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(AutocompletionServlet.class);
    public static final int MIN_LENGTH = 2;
    public static final int NUMBER_TO_SHOW = 10;

    private final AppLogic appLogic = AppSpringContext.get(AppLogic.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String text = req.getParameter("term");

        final List<String> strings;

        if (validForAutocompletions(text)) {
            strings = appLogic.autoCompleteText(BaseServlet.getSite(req), text, NUMBER_TO_SHOW);
        } else {
            strings = new LinkedList<String>();
        }

        JSONArray list = formJson(strings);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
//        resp.setContentType("text/plain");

        final PrintWriter writer = resp.getWriter();

        list.writeJSONString(writer);

        writer.flush();
    }

    @SuppressWarnings("unchecked")
    private JSONArray formJson(List<String> strings) {
        JSONArray list = new JSONArray();

        for (String string : strings) {
            JSONObject line = new JSONObject();

            line.put("value", string);

            list.add(line);
        }
        return list;
    }

    private boolean validForAutocompletions(String text) {
        return StringUtils.isNotEmpty(text) && text.length() >= MIN_LENGTH;
    }
}
