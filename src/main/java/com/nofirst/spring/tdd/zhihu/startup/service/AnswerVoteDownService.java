package com.nofirst.spring.tdd.zhihu.startup.service;


import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface AnswerVoteDownService {

    void store(Integer answerId, AccountUser accountUser);

    void destroy(Integer answerId, AccountUser accountUser);
}
