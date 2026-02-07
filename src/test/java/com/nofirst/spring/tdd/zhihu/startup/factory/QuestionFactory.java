package com.nofirst.spring.tdd.zhihu.startup.factory;


import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;

import java.util.Date;

public class QuestionFactory {

    public static Question createQuestion() {
        Date now = new Date();

        Question question = new Question();
        question.setUserId(1);
        question.setTitle("this is a question");
        question.setContent("this is content");
        question.setCreatedAt(now);
        question.setUpdatedAt(now);

        return question;
    }
}
