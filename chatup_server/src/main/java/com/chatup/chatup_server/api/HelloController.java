package com.chatup.chatup_server.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {  /* Rest endpoint for JWT testing */

    @RequestMapping({ "/hello" })
    public String hello() {
        return "Hello";
    }

}
