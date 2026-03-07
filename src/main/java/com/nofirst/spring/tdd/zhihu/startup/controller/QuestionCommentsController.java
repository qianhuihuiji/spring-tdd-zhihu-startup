package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.CommentVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionCommentService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/comments/questions/{questionId}")
    public CommonResult<PageInfo<CommentVo>> index(@PathVariable Integer questionId,
                                                   @RequestParam @NotNull Integer pageIndex,
                                                   @RequestParam @NotNull Integer pageSize) {
        PageInfo<CommentVo> pageInfo = questionCommentService.index(questionId, pageIndex, pageSize);
        return CommonResult.success(pageInfo);
    }
}
