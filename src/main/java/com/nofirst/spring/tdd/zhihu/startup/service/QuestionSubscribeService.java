package com.nofirst.spring.tdd.zhihu.startup.service;


import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface QuestionSubscribeService {

    void subscribe(Integer questionId, AccountUser accountUser);

    void unsubscribe(Integer questionId, AccountUser accountUser);
}
