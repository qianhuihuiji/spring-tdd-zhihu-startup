package com.nofirst.spring.tdd.zhihu.startup.factory;


import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.AnswerDto;

import java.util.Date;

public class AnswerFactory {

    public static AnswerDto createAnswerDto() {
        AnswerDto answer = new AnswerDto();
        answer.setContent("this is a answer");

        return answer;
    }

    public static Answer createAnswer(Integer questionId) {
        Date now = new Date();
        Answer answer = new Answer();
        answer.setId(1);
        answer.setQuestionId(questionId);
        answer.setUserId(1);
        answer.setCreatedAt(now);
        answer.setUpdatedAt(now);
        answer.setContent("this is a answer");

        return answer;
    }
}
