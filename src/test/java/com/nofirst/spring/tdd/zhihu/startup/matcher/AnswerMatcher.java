package com.nofirst.spring.tdd.zhihu.startup.matcher;

import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import org.mockito.ArgumentMatcher;

public class AnswerMatcher implements ArgumentMatcher<Answer> {

    private Answer left;

    public AnswerMatcher(Answer left) {
        this.left = left;
    }

    @Override
    public boolean matches(Answer right) {
        return left.getQuestionId().equals(right.getQuestionId()) &&
                left.getContent().equals(right.getContent()) &&
                left.getUserId().equals(right.getUserId());
    }
}
