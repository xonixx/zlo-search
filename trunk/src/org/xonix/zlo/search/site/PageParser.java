package org.xonix.zlo.search.site;

import org.apache.log4j.Logger;
import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.db.DbException;
import org.xonix.zlo.search.db.DbManager;
import org.xonix.zlo.search.model.ZloMessage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 20:17:07
 */
public class PageParser extends SiteSource {
    public static final Logger logger = Logger.getLogger(PageParser.class);

    private DbManager dbm;

    private Pattern MSG_REG_RE;
    private Pattern MSG_UNREG_RE;

    public PageParser(Site site) {
        super(site);
        dbm = site.getDbManager();
        MSG_REG_RE = Pattern.compile(site.getMSG_REG_RE_STR(), Pattern.DOTALL);
        MSG_UNREG_RE = Pattern.compile(site.getMSG_UNREG_RE_STR(), Pattern.DOTALL);
    }

    public ZloMessage parseMessage(String msg) throws PageParseException {
        ZloMessage message = new ZloMessage();

        Matcher m = MSG_UNREG_RE.matcher(msg);
        if (m.find()) {
            message.setReg(false);
        } else {
            m = MSG_REG_RE.matcher(msg);
            if (!m.find()) {
                if (msg.contains(getSite().getMSG_NOT_EXIST_OR_WRONG())) {
                    message.setStatus(ZloMessage.Status.DELETED);
                } else {
                    message.setStatus(ZloMessage.Status.UNKNOWN);
                    logger.warn("Can't parse msg... Possibly format changed!");
                }
                message.setSite(getSite());
                return message;
            }
            message.setReg(true);
        }

        message.setTopic(m.group(1));
        try {
            Integer topicCode = dbm.getTopicsHashMap().get(m.group(1));
            if (topicCode == null) {
                throw new PageParseException(msg);
            }
            message.setTopicCode(topicCode);
        } catch (DbException e) {
            logger.error(e);
        }

        message.setSite(getSite());
        message.setTitle(m.group(2));
        message.setNick(m.group(3));
        message.setHost(m.group(4));
        message.setDate(prepareDate(m.group(5)));
        message.setBody(m.group(6));
        message.setStatus(ZloMessage.Status.OK);

        return message;
    }

    public ZloMessage parseMessage(String msg, int urlNum) {
        ZloMessage zm = null;
        try {
            zm = parseMessage(msg);
        } catch (PageParseException e) {
            logger.error(MessageFormat.format("Unknown topic while parsing msg# {0}:\n {1}", urlNum, msg));
            System.exit(-1);
        }
        if (zm == null)
            return null;

        zm.setNum(urlNum);
        return zm;
    }

//    private String prepareTopic(String topic) {
//        return siteAccessor.WITHOUT_TOPIC.equals(topic) ? "" : topic;
//    }

    private static Date prepareDate(String s) {
        // s can be "Среда, Декабрь 5 13:35:25 2007<i>(Изменен: Среда, Декабрь 5 13:40:21 2007)</i>"
        final String[] RUS_MONTHS = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
                                    "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        DateFormat df = new SimpleDateFormat("M d hh:mm:ss yyyy");
        s = s.split("\\<(i|I)\\>")[0];
        s = s.split(",")[1].trim();
        for (int i=0; i<RUS_MONTHS.length; i++) {
            s = s.replaceFirst(RUS_MONTHS[i], Integer.toString(i+1));
        }
        Date d = null;
        try {
            d = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static void main(String[] args) throws IOException {
//        String s = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
//                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1251\" /><link rel=\"shortcut icon\" href=\"/favicon.ico\" /><link rel=\"stylesheet\" type=\"text/css\" href=\"/main.css\" /><meta http-equiv=\"Page-Exit\" content=\"progid:DXImageTransform.Microsoft.Fade(Duration=0.2)\" /><title>Форум-ФРТК-МФТИ : Добро пожаловать на новую старую борду</title></head><body>\n" +
//                "<script language=\"JavaScript\" type=\"text/javascript\">function popup(action, value, w, h){wnd=window.open(\"?\"+action+\"=\"+value,\"popup\",\"resizable=no,menubars=no,scrollbars=yes,width=\"+w+\",height=\"+h); }</script><div class=\"menu\"><A HREF=\"#1\">Перейти к ответам</A><A HREF=\"#Reply\">Ответить</A><A HREF=\"?index#1\" style=\"color:red;\">На главную страницу</A><a HREF=\"http://boards.alexzam.ru\">Поиск</A><A HREF=\"?register=form\">Регистрация</A><A HREF=\"?login=form\">Вход</A><A HREF=\"?rules\">Правила</A></div><BR><DIV ALIGN=CENTER><BIG>[без темы]</BIG>&nbsp;&nbsp;<BIG>Добро пожаловать на новую старую борду</BIG><BR>Сообщение было послано: <b>Bbsadmin</b><SMALL> (unreg)</SMALL> <small>(ignition.3ka.mipt.ru)</small><BR>Дата: Суббота, Апрель 7 14:16:27 2001</DIV><BR><br /><div class=\"body\"><P>Если появились какие то глюки с куками, сотрите их, они могли остаться от предыдущей борды..<P>IE: C:\\WINDOWS\\Cookies NN:<P>C:\\Program Files\\Netscape\\Users\\{user}\\cookies.txt</div><P></P><BR><CENTER><BIG>Сообщения в этом потоке</BIG></CENTER><DIV class=w><span id=m1><A NAME";
        //System.out.println(parseMessage(s));

//        for (int i=3764000; i<3764113; i++) {
//            System.out.println(parseMessage(PageRetriever.getPageContentByNumber(i), i));
//        }
//        System.out.println(prepareDate("Четверг, Май 31 21:52:27 2007"));
    }
}
