package info.xonix.zlo.search.logic.site;

import info.xonix.utils.Check;
import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.logic.ControlsDataLogic;
import info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf.WwwconfParams;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
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
    private ControlsDataLogic controlsDataLogic;

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(controlsDataLogic, "controlsDataLogic");
    }

    private Message parseMessage(String forumId, WwwconfParams wwwconfParams, Message message, String msg) throws PageParseException {

        Matcher m = wwwconfParams.getMsgUnregRe().matcher(msg);

        if (m.find()) {
            message.setReg(false);
        } else {
            m = wwwconfParams.getMsgRegRe().matcher(msg);
            if (!m.find()) {
                if (msg.contains(wwwconfParams.getMsgNotExistOrWrong())) {
                    message.setStatus(MessageStatus.DELETED);
                } else {
//                    message.setStatus(MessageStatus.UNKNOWN);
                    throw new PageParseException("Can't parse msg#:" + message.getNum() + " in site:" + forumId + "... Possibly format changed!\n\n" + msg);
                }
//                message.setSite(forumId);
                return message;
            }
            message.setReg(true);
        }

        List<Integer> groupsOrder = wwwconfParams.getMsgReGroupsOrder();
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

//        Integer topicCode = messagesDao.getTopicsHashMap(forumId).get(topic);
        Integer topicCode = controlsDataLogic.getTopicsReversedMap(forumId).get(topic);
        if (topicCode == null) {
            topicCode = -1;
            if (StringUtils.isNotEmpty(topic)) {
                log.info("Unknown topic: " + topic + " while parsing msg#: " + message.getNum() + " in site:" + forumId + "... Adding to title.");
                title = "[" + topic + "] " + title;
            }
        }
        message.setTopicCode(topicCode);

//        message.setSite(forumId);
        message.setTitle(title);
        message.setNick(StringUtils.trim(HtmlUtils.unescapeHtml(m.group(groupsOrder.get(3))))); // unescape nick
        message.setHost(m.group(groupsOrder.get(4)));

        String dateStr = m.group(groupsOrder.get(5));
        message.setDate(StringUtils.isEmpty(dateStr) ? null : prepareDate(wwwconfParams, dateStr));

        message.setBody(m.group(groupsOrder.get(6)));
        message.setStatus(MessageStatus.OK);

        return message;
    }

    public Message parseMessage(String forumId, WwwconfParams wwwconfParams, String msg, int urlNum) throws PageParseException {
        Message message = new Message();
        message.setNum(urlNum);
        parseMessage(forumId, wwwconfParams, message, msg);
        return message;
    }

    private Date prepareDate(WwwconfParams wwwconfParams, String s) {
        DateFormat df;
        Date d = null;
        try {
            String msgDatePattern = wwwconfParams.getMsgDatePattern();
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
            d = DateFormats.DF_BOARD_MSG.get().parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
