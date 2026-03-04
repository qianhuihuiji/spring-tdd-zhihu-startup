package com.nofirst.spring.tdd.zhihu.startup.service;


import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface QuestionVoteUpService {

    void store(Integer questionId, AccountUser accountUser);

    void destroy(Integer questionId, AccountUser accountUser);
}
