package com.nofirst.spring.tdd.zhihu.startup.mbg.mapper;


import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.UserCountVo;

import java.util.Date;
import java.util.List;


public interface AnswerMapperExt {

    List<Answer> selectByQuestionId(Integer questionId);

    List<UserCountVo> countActiveUser(Date beginTime);
}
