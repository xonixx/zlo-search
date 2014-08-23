package info.xonix.zlo.search.charts;

import info.xonix.utils.ExceptionUtils;
import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.dao.ChartsDao;
import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.model.ChartTask;
import info.xonix.zlo.search.utils.JsonUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * User: xonix
 * Date: 8/20/14
 * Time: 8:52 PM
 */
public class ChartServiceImpl implements ChartService {
    private static final Logger log = Logger.getLogger(ChartServiceImpl.class);

    @Autowired
    private MessagesDao messagesDao;

    @Autowired
    private ChartsDao chartsDao;

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

    @Override
    public Map<String, Integer> process(ChartTask task) {
        log.info("Going to process " + task);

        List<Date> messageDates = messagesDao.getMessageDates(
                task.getForumId(),
                task.getNicks(),
                task.getStart(),
                task.getEnd());

        Map<String, Integer> result = null;

        if (task.getType() == ChartType.ByHour) {
            result = prepareMapByDateFormat(messageDates, DateFormats.Hour.get());
        } else if (task.getType() == ChartType.DayInterval) {
            result = prepareMapByDateFormat(messageDates, DateFormats.ddMMyyyy_dots.get());
            // TODO merge to decrease points?
        } else if (task.getType() == ChartType.ByWeekDay) {
            Map<String, Integer> res = prepareMapByDateFormat(messageDates, DateFormats.WeekDay.get());
            result = new LinkedHashMap<String, Integer>();
            for (String weekDayName : WEEK_DAYS) {
                result.put(weekDayName, res.get(weekDayName));
            }
        }

        return result;
    }

    private BlockingQueue<ChartTask> queue = new ArrayBlockingQueue<ChartTask>(100, true);

    @Override
    public long submitTask(ChartTask task) {
        log.info("Submitting " + task);

        long id = chartsDao.insertChartTask(task);
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public void processNextTask() {
        try {
            ChartTask task = queue.take();
            try {
                Map<String, Integer> processedResult = process(task);
                chartsDao.saveChartTaskResult(task.getId(), JsonUtil.toJson(processedResult));
            } catch (Exception e) {
                log.error("Error processing " + task, e);
                chartsDao.saveChartTaskError(task.getId(), ExceptionUtils.getStackTrace(e));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
