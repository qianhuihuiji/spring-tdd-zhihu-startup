package com.nofirst.spring.tdd.zhihu.startup.service;


import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface CommentVoteUpService {

    void store(Integer commentId, AccountUser accountUser);

    void destroy(Integer commentId, AccountUser accountUser);
}
