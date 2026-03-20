package com.nofirst.spring.tdd.zhihu.startup.integration.questions;

import com.nofirst.spring.tdd.zhihu.startup.integration.AbstractVoteDownTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;

class DownVotesTest extends AbstractVoteDownTest {

    @Override
    protected String getResourceTypeName() {
        return Question.class.getSimpleName();
    }

    @Override
    protected String getResourcePath() {
        return "questions";
    }
}