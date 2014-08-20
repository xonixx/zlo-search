package info.xonix.zlo.search.charts;

import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.dao.MessagesDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.util.*;

/**
 * User: xonix
 * Date: 8/20/14
 * Time: 8:52 PM
 */
public class ChartService {
    @Autowired
    private MessagesDao messagesDao;

    private static String[] WEEK_DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private Map<String, Integer> prepareMapByDateFormat(List<Date> messageDates, DateFormat dateFormat) {
        Map<String, Integer> result = new TreeMap<String, Integer>();

        for (Date date : messageDates) {
            String hour = dateFormat.format(date);
            Integer cnt = result.get(hour);
            if (cnt == null) cnt = 0;
            result.put(hour, cnt + 1);
        }

        return result;
    }

    public Map<String, Integer> process(ChartTask task) {
        List<Date> messageDates = messagesDao.getMessageDates(
                task.forumId,
                task.nicks,
                task.start,
                task.end);

        Map<String, Integer> result = null;

        if (task.type == ChartType.ByHour) {
            result = prepareMapByDateFormat(messageDates, DateFormats.Hour.get());
        } else if (task.type == ChartType.DayInterval) {
            result = prepareMapByDateFormat(messageDates, DateFormats.ddMMyyyy_dots.get());
            // TODO merge to decrease points?
        } else if (task.type == ChartType.ByWeekDay) {
            Map<String, Integer> res = prepareMapByDateFormat(messageDates, DateFormats.WeekDay.get());
            result = new LinkedHashMap<String, Integer>();
            for (String weekDayName : WEEK_DAYS) {
                result.put(weekDayName, res.get(weekDayName));
            }
        }

        return result;
    }
}
