package com.nofirst.spring.tdd.zhihu.startup.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotExistedException;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotPublishedException;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.VoteMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.VoteMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.AnswerExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Vote;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.VoteExample;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.AnswerDto;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.VoteCountDto;
import com.nofirst.spring.tdd.zhihu.startup.model.enums.VoteActionType;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.AnswerVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final QuestionMapperExt questionMapperExt;
    private final VoteMapper voteMapper;
    private final VoteMapperExt voteMapperExt;

    @Override
    public PageInfo<AnswerVo> answers(Integer questionId, int pageIndex, int pageSize, AccountUser accountUser) {
        PageHelper.startPage(pageIndex, pageSize);
        AnswerExample example = new AnswerExample();
        example.createCriteria().andQuestionIdEqualTo(questionId);
        List<Answer> answers = answerMapper.selectByExample(example);
        PageInfo<Answer> answerPageInfo = new PageInfo<>(answers);
        List<AnswerVo> result = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerVo vo = new AnswerVo();
            vo.setId(answer.getId());
            vo.setQuestionId(answer.getQuestionId());
            vo.setUserId(answer.getUserId());
            vo.setCreatedAt(answer.getCreatedAt());
            vo.setUpdatedAt(answer.getUpdatedAt());
            vo.setContent(answer.getContent());
            result.add(vo);
        }
        appendVoteType(result, accountUser.getUserId());
        appendVoteCount(result);

        PageInfo<AnswerVo> pageResult = new PageInfo<>();
        pageResult.setTotal(answerPageInfo.getTotal());
        pageResult.setPageNum(answerPageInfo.getPageNum());
        pageResult.setPageSize(answerPageInfo.getPageSize());
        pageResult.setSize(answerPageInfo.getSize());
        pageResult.setList(result);

        return pageResult;
    }

    private void appendVoteCount(List<AnswerVo> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }

        List<Integer> answerIds = result.stream().map(AnswerVo::getId).toList();
        appendVoteUpCount(result, answerIds);
        appendVoteDownCount(result, answerIds);
    }


    private void appendVoteUpCount(List<AnswerVo> result, List<Integer> answerIds) {
        List<VoteCountDto> voteUpCountList = voteMapperExt.countByResource(Answer.class.getSimpleName(), VoteActionType.VOTE_UP.getCode(), answerIds);
        if (CollectionUtils.isEmpty(voteUpCountList)) {
            result.forEach(t -> t.setVoteUpCount(0));
            return;
        }

        Map<Integer, Integer> voteUpCountMap = voteUpCountList.stream().collect(Collectors.toMap(VoteCountDto::getResourceId, VoteCountDto::getVoteCount));
        result.forEach(t -> {
            t.setVoteUpCount(voteUpCountMap.getOrDefault(t.getId(), 0));
        });
    }

    private void appendVoteDownCount(List<AnswerVo> result, List<Integer> answerIds) {
        List<VoteCountDto> voteDownCountList = voteMapperExt.countByResource(Answer.class.getSimpleName(), VoteActionType.VOTE_DOWN.getCode(), answerIds);
        if (CollectionUtils.isEmpty(voteDownCountList)) {
            result.forEach(t -> t.setVoteDownCount(0));
            return;
        }

        Map<Integer, Integer> voteDownCountMap = voteDownCountList.stream().collect(Collectors.toMap(VoteCountDto::getResourceId, VoteCountDto::getVoteCount));
        result.forEach(t -> {
            t.setVoteDownCount(voteDownCountMap.getOrDefault(t.getId(), 0));
        });
    }

    private void appendVoteType(List<AnswerVo> result, Integer userId) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }

        List<Integer> answerIds = result.stream().map(AnswerVo::getId).toList();

        VoteExample voteExample = new VoteExample();
        voteExample.createCriteria()
                .andResourceIdIn(answerIds)
                .andUserIdEqualTo(userId)
                .andResourceTypeEqualTo(Answer.class.getSimpleName());
        List<Vote> votes = voteMapper.selectByExample(voteExample);
        if (CollectionUtils.isEmpty(votes)) {
            result.forEach(t -> t.setVoteType(VoteActionType.NOTHING.getCode()));
            return;
        }

        Map<Integer, Byte> answerActionMap = votes.stream().collect(Collectors.toMap(Vote::getResourceId, Vote::getActionType));
        result.forEach(t -> {
            if (answerActionMap.containsKey(t.getId())) {
                t.setVoteType(answerActionMap.get(t.getId()));
            } else {
                t.setVoteType(VoteActionType.NOTHING.getCode());
            }
        });
    }


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
    }

    @Override
    public void markAsBest(Integer answerId) {
        Answer answer = answerMapper.selectByPrimaryKey(answerId);
        questionMapperExt.markAsBestAnswer(answer.getQuestionId(), answer.getId());
    }

    @Override
    public void destroy(Integer answerId) {
        answerMapper.deleteByPrimaryKey(answerId);
    }
}