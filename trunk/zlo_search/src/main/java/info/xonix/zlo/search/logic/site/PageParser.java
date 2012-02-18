package info.xonix.zlo.search.logic.site;

import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.utils.Check;
import info.xonix.zlo.search.utils.HtmlUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Author: gubarkov
 * Date: 30.05.2007
 * Time: 20:17:07
 */
public class PageParser implements InitializingBean {
    public static final Logger log = Logger.getLogger(PageParser.class);

    @Autowired
    private MessagesDao messagesDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(messagesDao, "messagesDao");
    }

    private Message parseMessage(Site site, Message message, String msg) throws PageParseException {

        Matcher m = site.getMsgUnregRe().matcher(msg);

        if (m.find()) {
            message.setReg(false);
        } else {
            m = site.getMsgRegRe().matcher(msg);
            if (!m.find()) {
                if (msg.contains(site.getMsgNotExistOrWrong())) {
                    message.setStatus(MessageStatus.DELETED);
                } else {
                    message.setStatus(MessageStatus.UNKNOWN);
                    throw new PageParseException("Can't parse msg#:" + message.getNum() + " in site:" + site.getName() + "... Possibly format changed!\n\n" + msg);
                }
                message.setSite(site);
                return message;
            }
            message.setReg(true);
        }

        List<Integer> groupsOrder = site.getMsgReGroupsOrder();
        if (groupsOrder == null)
            groupsOrder = Arrays.asList(0,
                    1, // topic
                    2, // title
                    3, // nick
                    4, // host
                    5, // date
                    6  // body
            );
        else {
            groupsOrder = new ArrayList<Integer>(groupsOrder); // copy
            groupsOrder.add(0, 0);
        }

        String topic = m.group(groupsOrder.get(1));
        String title = m.group(groupsOrder.get(2));

        message.setTopic(topic);

        Integer topicCode = messagesDao.getTopicsHashMap(site).get(topic);
        if (topicCode == null) {
            topicCode = -1;
            if (StringUtils.isNotEmpty(topic)) {
                log.info("Unknown topic: " + topic + " while parsing msg#: " + message.getNum() + " in site:" + site.getName() + "... Adding to title.");
                title = "[" + topic + "] " + title;
            }
        }
        message.setTopicCode(topicCode);

        message.setSite(site);
        message.setTitle(title);
        message.setNick(StringUtils.trim(HtmlUtils.unescapeHtml(m.group(groupsOrder.get(3))))); // unescape nick
        message.setHost(m.group(groupsOrder.get(4)));

        String dateStr = m.group(groupsOrder.get(5));
        message.setDate(StringUtils.isEmpty(dateStr) ? new Date(0) : prepareDate(site, dateStr));

        message.setBody(m.group(groupsOrder.get(6)));
        message.setStatus(MessageStatus.OK);

        return message;
    }

    public Message parseMessage(Site site, String msg, int urlNum) throws PageParseException {
        Message message = new Message();
        message.setNum(urlNum);
        parseMessage(site, message, msg);
        return message;
    }

    private Date prepareDate(Site site, String s) {
        DateFormat df;
        Date d = null;
        try {
            String msgDatePattern = site.getMsgDatePattern();
            if (StringUtils.isNotEmpty(msgDatePattern)) {
                df = new SimpleDateFormat(msgDatePattern);
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
            d = DateFormats.DF_BOARD_MSG.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
