package io.kadai.example.wildfly.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

/** The loginError controller. */
@Controller
public class LoginErrorController {

  @GetMapping(path = "/loginerror")
  public RedirectView loginErrorGet() {
    return loginError();
  }

  @PostMapping(path = "/loginerror")
  public RedirectView loginErrorPost() {
    return loginError();
  }

  public RedirectView loginError() {
    return new RedirectView("/login?error", true);
  }
}
