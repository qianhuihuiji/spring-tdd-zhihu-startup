package com.nofirst.spring.tdd.zhihu.startup.mbg.mapper;

import com.nofirst.spring.tdd.zhihu.startup.model.vo.UserCountVo;

import java.util.Date;
import java.util.List;

public interface QuestionMapperExt {

    void markAsBestAnswer(Integer questionId, Integer answerId);

    void publish(Integer questionId, Date publishedAt);

    void updateAnswersCount(Integer questionId, Integer answersCount);

    void updateSlug(Integer questionId, String slug);

    List<UserCountVo> countActiveUser(Date beginTime);
}
