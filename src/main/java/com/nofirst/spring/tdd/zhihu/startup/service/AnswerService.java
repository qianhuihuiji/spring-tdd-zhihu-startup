package com.nofirst.spring.tdd.zhihu.startup.service;


import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.AnswerDto;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface AnswerService {

    PageInfo<Answer> answers(Integer questionId, int pageIndex, int pageSize);

    void store(Integer questionId, AnswerDto answerDto, AccountUser accountUser);

    void markAsBest(Integer answerId);

    void destroy(Integer answerId);
}
