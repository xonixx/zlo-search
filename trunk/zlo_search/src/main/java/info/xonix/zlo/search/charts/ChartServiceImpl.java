package info.xonix.zlo.search.charts;

import info.xonix.utils.ExceptionUtils;
import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.dao.ChartsDao;
import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.model.ChartTask;
import info.xonix.zlo.search.model.ChartTaskStatus;
import info.xonix.zlo.search.utils.DateUtil;
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
    public static final int OBSOLETE_AFTER_EVERY = 5000;
//    public static final int OBSOLETE_AFTER_EVERY = 10;

    @Autowired
    private MessagesDao messagesDao;

    @Autowired
    private ChartsDao chartsDao;

    private static String[] WEEK_DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private Map<String, Integer> prepareMapByDateFormat(List<Date> messageDates, DateFormat dateFormat) {
        Map<String, Integer> result = new HashMap<String, Integer>();

        for (Date date : messageDates) {
            String hour = dateFormat.format(date);
            Integer cnt = result.get(hour);
            if (cnt == null) cnt = 0;
            result.put(hour, cnt + 1);
        }

        return result;
    }

    private Map<String, Object> prepareDateIntervalData(List<Date> messageDates) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (messageDates.isEmpty()) {
            result.put("data", new int[0]);
            return result;
        }

        List<Date> datesNoTime = new ArrayList<Date>(messageDates.size());

        Calendar cal = Calendar.getInstance();

        for (Date messageDate : messageDates) {
            cal.setTime(messageDate);
            datesNoTime.add(DateUtil.emptyTimePart(cal));
        }

        Date min = messageDates.get(0);
        Date max = messageDates.get(messageDates.size() - 1);

        int[] data = new int[DateUtil.dateDiff(max, min) + 1];

        for (Date dateNoTime : datesNoTime) {
            data[DateUtil.dateDiff(dateNoTime, min)]++;
        }

        result.put("start", DateFormats.yyyyMMdd.get().format(min));
        result.put("data", data);

        return result;
    }

    @Override
    public Object process(ChartTask task) {
        log.info("Going to process " + task);

        List<Date> messageDates = messagesDao.getMessageDates(
                task.getForumId(),
                task.getNicks(),
                task.getStart(),
                task.getEnd());

        Object result = null;

        if (task.getType() == ChartType.ByHour) {
            Map<String, Integer> res = prepareMapByDateFormat(messageDates, DateFormats.Hour.get());
            Map<String, Integer> resultMap = new LinkedHashMap<String, Integer>();
            for (int i = 0; i <= 23; i++) {
                String si = String.valueOf(i);
                Integer value = res.get(si);
                resultMap.put(si, value != null ? value : 0);
            }
            result = resultMap;
        } else if (task.getType() == ChartType.ByWeekDay) {
            Map<String, Integer> res = prepareMapByDateFormat(messageDates, DateFormats.WeekDay.get());
            Map<String, Integer> resultMap = new LinkedHashMap<String, Integer>();
            for (String weekDayName : WEEK_DAYS) {
                Integer value = res.get(weekDayName);
                resultMap.put(weekDayName, value != null ? value : 0);
            }
            result = resultMap;
        } else if (task.getType() == ChartType.DayInterval) {
            result = prepareDateIntervalData(messageDates);
        }

        return result;
    }

    private BlockingQueue<ChartTask> queue = new ArrayBlockingQueue<ChartTask>(100, true);

    @Override
    public long submitTask(ChartTask task) {
        ChartTask chartTask = chartsDao.loadChartTask(task.getDescriptor());
        if (chartTask != null) {
            log.info("Chart already submitted: " + chartTask);
            return chartTask.getId();
        }

        log.info("Submitting " + task);

        task.setStatus(ChartTaskStatus.NEW);
        long id = chartsDao.insertChartTask(task);
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (id % OBSOLETE_AFTER_EVERY == 0) {
            long idToObsolete = id - OBSOLETE_AFTER_EVERY;
            log.info("Removing obsolete charts data for ids < " + idToObsolete);
            chartsDao.removeTasksLessThen(idToObsolete);
        }

        return id;
    }

    @Override
    public ChartTask loadChartTask(String descriptor) {
        ChartTask chartTask = chartsDao.loadChartTask(descriptor);

        if (chartTask != null)
            return chartTask;

        ChartTask notFound = new ChartTask();
        notFound.setError("График не найден");
        return notFound;
    }

    @Override
    public void processNextTask() {
        try {
            ChartTask task = queue.take();
            task.setStatus(ChartTaskStatus.STARTED);
            chartsDao.updateChartTask(task);
            try {
                Object processedResult = process(task);

                task.setStatus(ChartTaskStatus.READY);
                task.setResult(JsonUtil.toJson(processedResult));

                chartsDao.updateChartTask(task);

                log.info("Processed for task.id=" + task.getId());
            } catch (Exception e) {
                log.error("Error processing " + task, e);

                task.setStatus(ChartTaskStatus.READY);
                task.setError(ExceptionUtils.getStackTrace(e));

                chartsDao.updateChartTask(task);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
