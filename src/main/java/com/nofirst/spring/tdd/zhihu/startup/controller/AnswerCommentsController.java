package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerCommentService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
public class AnswerCommentsController {

    private final AnswerCommentService answerCommentService;

    @PostMapping("/comments/answers/{answerId}")
    public CommonResult<String> store(@PathVariable Integer answerId,
                                      @Validated @RequestBody CommentDto commentDto,
                                      @AuthenticationPrincipal AccountUser accountUser) {
        answerCommentService.comment(answerId, commentDto, accountUser);
        return CommonResult.success("ok");
    }
}
