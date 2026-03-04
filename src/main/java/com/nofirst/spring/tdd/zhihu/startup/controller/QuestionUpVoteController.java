package com.nofirst.spring.tdd.zhihu.startup.controller;


import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionVoteUpService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class QuestionUpVoteController {

    private final QuestionVoteUpService questionVoteUpService;

    @PostMapping("/questions/{questionId}/up-votes")
    public CommonResult<String> store(@PathVariable Integer questionId, @AuthenticationPrincipal AccountUser accountUser) {
        questionVoteUpService.store(questionId, accountUser);
        return CommonResult.success("ok");
    }

    @DeleteMapping("/questions/{questionId}/up-votes")
    public CommonResult<String> destroy(@PathVariable Integer questionId, @AuthenticationPrincipal AccountUser accountUser) {
        questionVoteUpService.destroy(questionId, accountUser);
        return CommonResult.success("ok");
    }
}
