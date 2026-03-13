package com.nofirst.spring.tdd.zhihu.startup.service;

import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;

public interface QuestionService {

    QuestionVo show(Integer id);

}