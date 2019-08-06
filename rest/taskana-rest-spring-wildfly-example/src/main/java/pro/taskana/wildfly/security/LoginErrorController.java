package pro.taskana.wildfly.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * The loginError controller.
 */
@Controller
public class LoginErrorController {

    @PostMapping
    @GetMapping
    @RequestMapping("/loginerror")
    public RedirectView loginError() {
        return new RedirectView("/login?error", true);
    }
}
