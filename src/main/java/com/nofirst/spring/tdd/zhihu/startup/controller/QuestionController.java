package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.QuestionDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionService;
import com.nofirst.spring.tdd.zhihu.startup.validator.ValidCategory;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Validated
public class QuestionController {

    private QuestionService questionService;

    @GetMapping("/questions")
    public void index() {
        // do nothing here
    }

    @GetMapping("/questions/{id}")
    public CommonResult<QuestionVo> show(@PathVariable Integer id) {
        QuestionVo questionVo = questionService.show(id);
        return CommonResult.success(questionVo);
    }


    @PostMapping("/questions")
    public CommonResult<String> store(@RequestBody @ValidCategory QuestionDto dto, @AuthenticationPrincipal AccountUser accountUser) {
        questionService.store(dto, accountUser);
        return CommonResult.success("ok");
    }
}
