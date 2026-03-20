package com.nofirst.spring.tdd.zhihu.startup.service.impl;

import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.VoteMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Vote;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.VoteExample;
import com.nofirst.spring.tdd.zhihu.startup.model.enums.VoteActionType;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.GenericVoteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class GenericVoteServiceImpl implements GenericVoteService {

    private VoteMapper voteMapper;

    @Override
    public void voteUp(String resourceType, Integer resourceId, AccountUser accountUser) {
        store(resourceType, resourceId, accountUser, VoteActionType.VOTE_UP);
    }

    @Override
    public void cancelVoteUp(String resourceType, Integer resourceId, AccountUser accountUser) {
        destroy(resourceType, resourceId, accountUser, VoteActionType.VOTE_UP);
    }

    @Override
    public void voteDown(String resourceType, Integer resourceId, AccountUser accountUser) {
        store(resourceType, resourceId, accountUser, VoteActionType.VOTE_DOWN);
    }

    @Override
    public void cancelVoteDown(String resourceType, Integer resourceId, AccountUser accountUser) {
        destroy(resourceType, resourceId, accountUser, VoteActionType.VOTE_DOWN);
    }

    private void store(String resourceType, Integer resourceId, AccountUser accountUser, VoteActionType actionType) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andUserIdEqualTo(accountUser.getUserId());
        criteria.andResourceIdEqualTo(resourceId);
        criteria.andResourceTypeEqualTo(resourceType);
        long count = voteMapper.countByExample(voteExample);

        Date now = new Date();
        Vote vote = new Vote();
        if (count == 0) {
            vote.setUserId(accountUser.getUserId());
            vote.setResourceId(resourceId);
            vote.setResourceType(resourceType);
            vote.setActionType(actionType.getCode());

            vote.setCreatedAt(now);
            vote.setUpdatedAt(now);

            voteMapper.insert(vote);
        } else {
            vote.setActionType(actionType.getCode());
            vote.setUpdatedAt(now);
            voteMapper.updateByExampleSelective(vote, voteExample);
        }
    }

    private void destroy(String resourceType, Integer resourceId, AccountUser accountUser, VoteActionType actionType) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andResourceIdEqualTo(resourceId);
        criteria.andResourceTypeEqualTo(resourceType);
        criteria.andActionTypeEqualTo(actionType.getCode());
        criteria.andUserIdEqualTo(accountUser.getUserId());
        voteMapper.deleteByExample(voteExample);
    }
}