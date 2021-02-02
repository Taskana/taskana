package pro.taskana.example.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** The login controller. */
@Controller
public class LoginController implements WebMvcConfigurer {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    LOGGER.debug("Entry to addViewControllers()");
    registry.addViewController("/login").setViewName("login");
    registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    LOGGER.debug("Exit from addViewControllers()");
  }
}
