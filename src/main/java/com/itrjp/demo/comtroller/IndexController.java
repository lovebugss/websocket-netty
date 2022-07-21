package com.itrjp.demo.comtroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IndexController
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/21 11:26
 */
@Controller
@RequestMapping
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
}