package com.nofirst.spring.tdd.zhihu.startup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionController {

    /**
     * Index common result.
     *
     */
    @GetMapping("/questions")
    public void index() {
        // do nothing here
    }
}
