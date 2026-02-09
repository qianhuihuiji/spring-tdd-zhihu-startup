package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
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
}
