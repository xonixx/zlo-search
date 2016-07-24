package info.xonix.zlo.web.servlets;

import info.xonix.zlo.web.logic.ImgLogic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ImgPreviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getParameter("url");
        String hash = req.getParameter("hash");

        if (!ImgLogic.checkHash(url, hash)) {
            resp.setStatus(403);
            return;
        }

        ImgLogic.renderPreviewAndCache(url, resp, getServletContext());
    }
}
