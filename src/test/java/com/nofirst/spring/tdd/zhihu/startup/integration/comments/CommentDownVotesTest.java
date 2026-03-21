package com.nofirst.spring.tdd.zhihu.startup.integration.comments;

import com.nofirst.spring.tdd.zhihu.startup.integration.AbstractVoteDownTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.CommentMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.VoteMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Comment;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.CommentExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.VoteExample;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

class CommentDownVotesTest extends AbstractVoteDownTest {

    @Autowired
    private VoteMapper voteMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    protected String getResourceTypeName() {
        return Comment.class.getSimpleName();
    }

    @Override
    protected String getResourcePath() {
        return "comments";
    }

    @BeforeEach
    public void setupTestData() {
        VoteExample voteExample = new VoteExample();
        voteExample.createCriteria();
        voteMapper.deleteByExample(voteExample);
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria();
        commentMapper.deleteByExample(commentExample);
    }
}