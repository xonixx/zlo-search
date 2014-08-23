package info.xonix.zlo.web.controllers;

import info.xonix.zlo.search.charts.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User: xonix
 * Date: 8/23/14
 * Time: 2:51 PM
 */
@Controller
public class ChartController {
    @Autowired
    private ChartService chartService;

    @RequestMapping("/helloWorld")
    public String helloWorld(Model model) {
        model.addAttribute("message", "Hello World!");
        return "helloWorld";
    }
}
