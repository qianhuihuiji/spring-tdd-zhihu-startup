package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class QuestionController {

    @GetMapping("/questions")
    public CommonResult<List<Object>> index() {
        // 返回空列表
        return CommonResult.success(Collections.emptyList());
    }
}