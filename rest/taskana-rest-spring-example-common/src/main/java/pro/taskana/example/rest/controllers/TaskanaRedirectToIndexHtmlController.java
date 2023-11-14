package pro.taskana.example.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** The view controller. */
@Controller
public class TaskanaRedirectToIndexHtmlController {

  @GetMapping(path = {"", "taskana/**"})
  public String index() {
    return "redirect:/index.html";
  }
}
