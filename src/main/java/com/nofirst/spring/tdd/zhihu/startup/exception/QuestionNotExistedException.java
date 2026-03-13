package com.nofirst.spring.tdd.zhihu.startup.exception;

import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;

public class QuestionNotExistedException extends ApiException {

    public QuestionNotExistedException() {
        super(ResultCode.FAILED, "question not exist");
    }
}