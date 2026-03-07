package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionCommentService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
public class QuestionCommentsController {

    private QuestionCommentService questionCommentService;

    @PostMapping("/comments/questions/{questionId}")
    @PreAuthorize("@questionPolicy.canComment(#questionId)")
    public CommonResult<String> store(@PathVariable Integer questionId,
                                      @RequestBody @Validated CommentDto commentDto,
                                      @AuthenticationPrincipal AccountUser accountUser) {
        questionCommentService.comment(questionId, commentDto, accountUser);
        return CommonResult.success("ok");
    }
}
