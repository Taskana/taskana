package io.kadai.example.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** The view controller. */
@Controller
public class KadaiRedirectToIndexHtmlController {

  @GetMapping(path = {"", "kadai/**"})
  public String index() {
    return "redirect:/index.html";
  }
}
