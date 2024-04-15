package com.lily.nativecodesandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lily via on 2024/4/11 21:22
 */
@RestController("/")
public class codeController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, 世界!";
    }
}
