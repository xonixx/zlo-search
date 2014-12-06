package info.xonix.zlo.web.controllers;

import info.xonix.zlo.search.charts.ChartService;
import info.xonix.zlo.search.model.ChartTask;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: xonix
 * Date: 8/23/14
 * Time: 2:51 PM
 */
@Controller
@RequestMapping("/charts")
public class ChartController {
    private static final Logger log = Logger.getLogger(ChartController.class);

    @Autowired
    private ChartService chartService;

    @RequestMapping("")
    public String charts(Model model) {
        List<ChartTask> lastTasks = chartService.getLastTasks(15);
        for (ChartTask lastTask : lastTasks) {
            lastTask.setResult(null);
        }
        model.addAttribute("lastTasks", lastTasks);
        return "charts";
    }

    @RequestMapping("/submitTask")
    @ResponseBody
    public Map submitTask(@RequestBody ChartTask chartTask) {
        log.info("Submitted: " + chartTask);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("id", chartService.submitTask(chartTask));
        result.put("descriptor", chartTask.getDescriptor());

        return result;
    }

    @RequestMapping("/checkTask")
    @ResponseBody
    public ChartTask checkTask(@RequestBody ChartTask chartTask) {
        ChartTask task = chartService.loadChartTask(chartTask.getDescriptor());
        if (task == null) {
            log.info("checkTask: task not found, submitting: " + chartTask);
            chartService.submitTask(chartTask);
            return chartTask;
        }
        return task;
    }
}
