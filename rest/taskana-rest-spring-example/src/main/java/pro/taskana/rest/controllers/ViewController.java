package pro.taskana.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping({"/administration*/**", "/workplace*/**", "/monitor*/**", "/no-role*/**"})
    public String index() {
        return "forward:/index.html";
    }
}
