package com.nofirst.spring.tdd.zhihu.startup.factory;

import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.QuestionDto;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuestionFactory {

    public static Question createPublishedQuestion() {
        Date lastWeek = DateUtils.addWeeks(new Date(), -1);
        Date now = new Date();

        Question question = new Question();
        question.setUserId(1);
        question.setTitle("this is a question");
        question.setContent("this is content");
        question.setCreatedAt(now);
        question.setUpdatedAt(now);
        question.setPublishedAt(lastWeek);
        question.setCategoryId(1);
        question.setAnswersCount(0);

        return question;
    }

    public static Question createUnpublishedQuestion() {
        Date now = new Date();

        Question question = new Question();
        question.setUserId(1);
        question.setTitle("this is a question");
        question.setContent("this is content");
        question.setCreatedAt(now);
        question.setUpdatedAt(now);
        question.setPublishedAt(null);
        question.setCategoryId(1);
        question.setAnswersCount(0);

        return question;
    }


    public static QuestionDto createQuestionDto() {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle("this is a new question");
        questionDto.setContent("question content");
        questionDto.setCategoryId(1);

        return questionDto;
    }

    public static List<Question> createPublishedQuestionBatch(Integer times) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            questions.add(createPublishedQuestion());
        }

        return questions;
    }
}
