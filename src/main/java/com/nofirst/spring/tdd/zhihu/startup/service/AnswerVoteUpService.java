package com.nofirst.spring.tdd.zhihu.startup.service;


import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface AnswerVoteUpService {

    void store(Integer answerId, AccountUser accountUser);
}
