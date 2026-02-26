package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class BestAnswerController {

    private final AnswerService answerService;

    @PostMapping("/answers/{answerId}/best")
    public CommonResult<String> store(@PathVariable Integer answerId) {
        answerService.markAsBest(answerId);
        return CommonResult.success("ok");
    }
}
