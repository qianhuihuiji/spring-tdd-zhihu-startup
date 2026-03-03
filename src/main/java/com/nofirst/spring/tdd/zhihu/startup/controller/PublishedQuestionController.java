package com.nofirst.spring.tdd.zhihu.startup.controller;


import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PublishedQuestionController {

    private final QuestionService questionService;

    @PostMapping("/questions/{questionId}/published-questions")
    @PreAuthorize("@questionPolicy.isQuestionOwner(#questionId, #accountUser)")
    public CommonResult<String> store(@PathVariable Integer questionId, @AuthenticationPrincipal AccountUser accountUser) {
        questionService.publish(questionId);
        return CommonResult.success("ok");
    }
}
