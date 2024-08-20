package io.kadai.example.wildfly.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

/** The logout controller. */
@Controller
public class LogoutController {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);

  @GetMapping(path = "/logout")
  public RedirectView loginErrorGet(HttpServletRequest request) {
    return logout(request);
  }

  @PostMapping(path = "/logout")
  public RedirectView loginErrorPost(HttpServletRequest request) {
    return logout(request);
  }

  public RedirectView logout(HttpServletRequest request) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Logging out...");
    }

    if (request.getSession(false) != null) {
      request.getSession(false).invalidate(); // remove session.
    }
    if (request.getSession() != null) {
      request.getSession().invalidate(); // remove session.
    }

    try {
      request.logout();
    } catch (ServletException e) {
      LOGGER.warn("Exception caught while logging out: {}", e.getMessage());
    }

    return new RedirectView("/", true);
  }
}
