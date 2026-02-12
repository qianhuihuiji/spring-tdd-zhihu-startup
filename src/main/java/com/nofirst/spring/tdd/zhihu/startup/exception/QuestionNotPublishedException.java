package com.nofirst.spring.tdd.zhihu.startup.exception;


import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;


public class QuestionNotPublishedException extends ApiException {
    
    public QuestionNotPublishedException() {
        super(ResultCode.FAILED, "question not publish");
    }
}
