package com.nofirst.spring.tdd.zhihu.startup.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotExistedException;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotPublishedException;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.VoteMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.VoteExample;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.AnswerDto;
import com.nofirst.spring.tdd.zhihu.startup.model.enums.VoteActionType;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerMapper answerMapper;
    private final AnswerMapperExt answerMapperExt;
    private final QuestionMapper questionMapper;
    private final QuestionMapperExt questionMapperExt;
    private final VoteMapper voteMapper;

    @Override
    public PageInfo<Answer> answers(Integer questionId, int pageIndex, int pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        List<Answer> answers = answerMapperExt.selectByQuestionId(questionId);
        return new PageInfo<>(answers);
    }

    @Override
    public void store(Integer questionId, AnswerDto answerDto, AccountUser accountUser) {
        Question question = questionMapper.selectByPrimaryKey(questionId);
        if (Objects.isNull(question)) {
            throw new QuestionNotExistedException();
        }
        if (Objects.isNull(question.getPublishedAt())) {
            throw new QuestionNotPublishedException();
        }
        Date now = new Date();
        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setUserId(accountUser.getUserId());
        answer.setCreatedAt(now);
        answer.setUpdatedAt(now);
        answer.setContent(answerDto.getContent());

        answerMapper.insert(answer);

        questionMapperExt.updateAnswersCount(questionId, question.getAnswersCount() + 1);
    }


    @Override
    public void markAsBest(Integer answerId) {
        Answer answer = answerMapper.selectByPrimaryKey(answerId);
        questionMapperExt.markAsBestAnswer(answer.getQuestionId(), answer.getId());
    }

    @Override
    public void destroy(Integer answerId) {
        Answer answer = answerMapper.selectByPrimaryKey(answerId);
        Question question = questionMapper.selectByPrimaryKey(answer.getQuestionId());
        questionMapperExt.updateAnswersCount(answer.getQuestionId(), question.getAnswersCount() - 1);
        answerMapper.deleteByPrimaryKey(answerId);
    }

    @Override
    public Boolean isVotedUp(Integer answerId) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andVotedIdEqualTo(answerId);
        criteria.andResourceTypeEqualTo(Answer.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_UP.getCode());
        long count = voteMapper.countByExample(voteExample);
        return count > 0;
    }

    @Override
    public long upVotesCount(Integer answerId) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andVotedIdEqualTo(answerId);
        criteria.andResourceTypeEqualTo(Answer.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_UP.getCode());
        return voteMapper.countByExample(voteExample);
    }

    @Override
    public Boolean isVotedDown(Integer answerId) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andVotedIdEqualTo(answerId);
        criteria.andResourceTypeEqualTo(Answer.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_DOWN.getCode());
        long count = voteMapper.countByExample(voteExample);
        return count > 0;
    }

    @Override
    public long downVotesCount(Integer answerId) {
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andVotedIdEqualTo(answerId);
        criteria.andResourceTypeEqualTo(Answer.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_DOWN.getCode());
        return voteMapper.countByExample(voteExample);
    }
}
