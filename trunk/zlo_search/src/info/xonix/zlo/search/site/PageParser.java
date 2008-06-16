package info.xonix.zlo.search.site;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.db.DbException;
import info.xonix.zlo.search.db.DbManager;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.utils.HtmlUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
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
    private String MSG_DATE_PATTERN;

    public PageParser(Site site) {
        super(site);
        dbm = site.getDbManager();
        MSG_REG_RE = Pattern.compile(site.getMSG_REG_RE_STR(), Pattern.DOTALL);
        MSG_UNREG_RE = Pattern.compile(site.getMSG_UNREG_RE_STR(), Pattern.DOTALL);
        MSG_DATE_PATTERN = site.getMSG_DATE_PATTERN();
    }

    public ZloMessage parseMessage(ZloMessage message, String msg) {

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
                    throw new PageParseException("Can't parse msg#:" + message.getNum() + " in site:" + getSiteName() + "... Possibly format changed!\n\n" + msg);
                }
                message.setSite(getSite());
                return message;
            }
            message.setReg(true);
        }

        List<Integer> groupsOrder = getSite().getMSG_RE_GROUPS_ORDER();
        if (groupsOrder == null)
            groupsOrder = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
        else {
            groupsOrder = new ArrayList<Integer>(groupsOrder); // copy
            groupsOrder.add(0, 0);
        }


        String topic = m.group(groupsOrder.get(1));
        String title = m.group(groupsOrder.get(2));

        message.setTopic(topic);
        try {
            Integer topicCode = dbm.getTopicsHashMap().get(topic);
            if (topicCode == null) {
                topicCode = -1;
                if (StringUtils.isNotEmpty(topic)) {
                    logger.info("Unknown topic: " + topic + " while parsing msg#: " + message.getNum() + " in site:" + getSiteName() + "... Adding to title.");
                    title = "[" + topic + "] " + title;
                }
            }
            message.setTopicCode(topicCode);
        } catch (DbException e) {
            logger.error(e);
        }

        message.setSite(getSite());
        message.setTitle(title);
        message.setNick(StringUtils.trim(HtmlUtils.unescapeHtml(m.group(groupsOrder.get(3))))); // unescape nick
        message.setHost(m.group(groupsOrder.get(4)));
        message.setDate(prepareDate(m.group(groupsOrder.get(5))));
        message.setBody(m.group(groupsOrder.get(6)));
        message.setStatus(ZloMessage.Status.OK);

        return message;
    }

    public ZloMessage parseMessage(String msg, int urlNum) {
        ZloMessage zm = new ZloMessage();
        zm.setNum(urlNum);
        parseMessage(zm, msg);
        return zm;
    }

    private Date prepareDate(String s) {
        DateFormat df;
        Date d = null;
        try {
            if (StringUtils.isNotEmpty(MSG_DATE_PATTERN)) {
                df = new SimpleDateFormat(MSG_DATE_PATTERN);
                return df.parse(s);
            }

            // s can be "Среда, Декабрь 5 13:35:25 2007<i>(Изменен: Среда, Декабрь 5 13:40:21 2007)</i>"
            final String[] RUS_MONTHS = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
                    "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
            s = s.split("\\<(i|I)\\>")[0];
            s = s.split(",")[1].trim();
            for (int i = 0; i < RUS_MONTHS.length; i++) {
                s = s.replaceFirst(RUS_MONTHS[i], Integer.toString(i + 1));
            }
            d = Config.DateFormats.DF_BOARD_MSG.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
