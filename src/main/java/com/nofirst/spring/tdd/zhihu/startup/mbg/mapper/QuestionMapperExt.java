package com.nofirst.spring.tdd.zhihu.startup.mbg.mapper;

public interface QuestionMapperExt {

    void markAsBestAnswer(Integer questionId, Integer answerId);
}
