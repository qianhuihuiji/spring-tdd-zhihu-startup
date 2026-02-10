package com.nofirst.spring.tdd.zhihu.startup.service;


import com.nofirst.spring.tdd.zhihu.startup.model.dto.AnswerDto;

public interface AnswerService {

    void store(Integer questionId, AnswerDto answerDto);
}
