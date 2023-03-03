/*-
 * #%L
 * pro.taskana:taskana-rest-spring-example-wildfly
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.example.wildfly.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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
