package com.nofirst.spring.tdd.zhihu.startup.mbg.mapper;


import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;

import java.util.List;


public interface AnswerMapperExt {

    List<Answer> selectByQuestionId(Integer questionId);
}
