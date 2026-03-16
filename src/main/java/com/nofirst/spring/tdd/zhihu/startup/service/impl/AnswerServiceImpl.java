package com.nofirst.spring.tdd.zhihu.startup.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotExistedException;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotPublishedException;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.AnswerExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.AnswerDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.AnswerVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final QuestionMapperExt questionMapperExt;

    @Override
    public PageInfo<AnswerVo> answers(Integer questionId, int pageIndex, int pageSize) {
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
        PageInfo<AnswerVo> pageResult = new PageInfo<>();
        pageResult.setTotal(answerPageInfo.getTotal());
        pageResult.setPageNum(answerPageInfo.getPageNum());
        pageResult.setPageSize(answerPageInfo.getPageSize());
        pageResult.setSize(answerPageInfo.getSize());
        pageResult.setList(result);

        return pageResult;
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