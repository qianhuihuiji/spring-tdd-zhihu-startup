package com.nofirst.spring.tdd.zhihu.startup.controller;


import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.CommentVoteUpService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class CommentUpVoteController {

    private final CommentVoteUpService commentVoteUpService;

    @PostMapping("/comments/{commentId}/up-votes")
    public CommonResult<String> store(@PathVariable Integer commentId, @AuthenticationPrincipal AccountUser accountUser) {
        commentVoteUpService.store(commentId, accountUser);
        return CommonResult.success("ok");
    }

    @DeleteMapping("/comments/{commentId}/up-votes")
    public CommonResult<String> destroy(@PathVariable Integer commentId, @AuthenticationPrincipal AccountUser accountUser) {
        commentVoteUpService.destroy(commentId, accountUser);
        return CommonResult.success("ok");
    }
}
