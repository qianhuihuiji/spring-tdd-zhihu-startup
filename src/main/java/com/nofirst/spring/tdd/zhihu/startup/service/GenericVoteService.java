package com.nofirst.spring.tdd.zhihu.startup.service;

import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface GenericVoteService {

    void voteUp(String resourceType, Integer resourceId, AccountUser accountUser);

    void cancelVoteUp(String resourceType, Integer resourceId, AccountUser accountUser);

    void voteDown(String resourceType, Integer resourceId, AccountUser accountUser);
    
    void cancelVoteDown(String resourceType, Integer resourceId, AccountUser accountUser);
}