package pro.taskana.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The view controller.
 */
@Controller
public class ViewController {

    @RequestMapping({"", "taskana/**"})
    public String index() {
        return "forward:/index.html";
    }
}
