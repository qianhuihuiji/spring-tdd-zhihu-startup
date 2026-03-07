package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.QuestionDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionService;
import com.nofirst.spring.tdd.zhihu.startup.validator.ValidCategory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Validated
public class QuestionController {

    private QuestionService questionService;

    @GetMapping("/questions")
    public CommonResult<PageInfo<QuestionVo>> index(@RequestParam @NotNull Integer pageIndex,
                                                    @RequestParam @NotNull Integer pageSize,
                                                    @RequestParam(required = false) String slug,
                                                    @RequestParam(required = false) String by,
                                                    @RequestParam(required = false) Integer popularity,
                                                    @RequestParam(required = false) Integer unanswered) {
        PageInfo<QuestionVo> questionPage = questionService.index(pageIndex, pageSize, slug, by, popularity, unanswered);
        return CommonResult.success(questionPage);
    }

    @GetMapping(value = {
            "/questions/{id}",
            "/questions/{id}/{slug:.*}"
    })
    public CommonResult<QuestionVo> show(@PathVariable Integer id,
                                         @PathVariable(required = false) String slug) {
        QuestionVo questionVo = questionService.show(id);
        return CommonResult.success(questionVo);
    }


    @PostMapping("/questions")
    public CommonResult<String> store(@RequestBody @ValidCategory QuestionDto dto, @AuthenticationPrincipal AccountUser accountUser) {
        questionService.store(dto, accountUser);
        return CommonResult.success("ok");
    }
}
