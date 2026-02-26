package com.nofirst.spring.tdd.zhihu.startup.exception;


import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;

public class AnswerNotExistedException extends ApiException {
    
    public AnswerNotExistedException() {
        super(ResultCode.FAILED, "answer not exist");
    }
}
