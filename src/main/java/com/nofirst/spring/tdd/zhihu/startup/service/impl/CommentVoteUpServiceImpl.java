package com.nofirst.spring.tdd.zhihu.startup.service.impl;


import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.VoteMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Comment;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Vote;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.VoteExample;
import com.nofirst.spring.tdd.zhihu.startup.model.enums.VoteActionType;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.CommentVoteUpService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class CommentVoteUpServiceImpl implements CommentVoteUpService {

    private VoteMapper voteMapper;

    @Override
    public void store(Integer commentId, AccountUser accountUser) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andVotedIdEqualTo(commentId);
        criteria.andResourceTypeEqualTo(Comment.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_UP.getCode());
        long count = voteMapper.countByExample(voteExample);
        if (count == 0) {
            Vote vote = new Vote();
            vote.setUserId(accountUser.getUserId());
            vote.setVotedId(commentId);
            vote.setResourceType(Comment.class.getSimpleName());
            vote.setActionType(VoteActionType.VOTE_UP.getCode());
            Date now = new Date();
            vote.setCreatedAt(now);
            vote.setUpdatedAt(now);

            voteMapper.insert(vote);
        }
    }

    @Override
    public void destroy(Integer commentId, AccountUser accountUser) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andVotedIdEqualTo(commentId);
        criteria.andResourceTypeEqualTo(Comment.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_UP.getCode());
        voteMapper.deleteByExample(voteExample);
    }
}
