package info.xonix.zlo.web.controllers;

import info.xonix.zlo.search.charts.ChartService;
import info.xonix.zlo.search.model.ChartTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * User: xonix
 * Date: 8/23/14
 * Time: 2:51 PM
 */
@Controller
@RequestMapping("/charts")
public class ChartController {
    @Autowired
    private ChartService chartService;

    @RequestMapping("/index")
    public String helloWorld(Model model) {
        return "charts";
    }

    @RequestMapping("/submitTask")
    @ResponseBody
    public Map doSomeThing(@RequestBody ChartTask chartTask) {
        System.out.println("Submitted: " + chartTask);
        Map result = new HashMap();
        result.put("id", chartService.submitTask(chartTask));
        return result;
    }
}
