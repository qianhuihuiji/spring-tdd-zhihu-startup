package com.nofirst.spring.tdd.zhihu.startup.mbg.mapper;

import java.util.Date;

public interface QuestionMapperExt {

    void markAsBestAnswer(Integer questionId, Integer answerId);

    void publish(Integer questionId, Date publishedAt);

    void updateAnswersCount(Integer questionId, Integer answersCount);
}
