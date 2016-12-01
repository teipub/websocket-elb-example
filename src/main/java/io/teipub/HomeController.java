package io.teipub;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by tei on 2016-11-28.
 * teipub.io
 */
@Controller
@RequestMapping
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "home";
    }
}
