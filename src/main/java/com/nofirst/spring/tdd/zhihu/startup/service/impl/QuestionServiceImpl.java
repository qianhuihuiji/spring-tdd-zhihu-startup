package com.nofirst.spring.tdd.zhihu.startup.service.impl;

import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotExistedException;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotPublishedException;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.QuestionDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import com.nofirst.spring.tdd.zhihu.startup.publisher.CustomEventPublisher;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerService;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private QuestionMapper questionMapper;
    private AnswerService answerService;
    
    private CustomEventPublisher customEventPublisher;


    @Override
    public QuestionVo show(Integer id, AccountUser accountUser) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (Objects.isNull(question)) {
            throw new QuestionNotExistedException();
        }
        if (Objects.isNull(question.getPublishedAt())) {
            throw new QuestionNotPublishedException();
        }

        QuestionVo questionVo = new QuestionVo();
        questionVo.setId(question.getId());
        questionVo.setUserId(question.getUserId());
        questionVo.setTitle(question.getTitle());
        questionVo.setContent(question.getContent());
        questionVo.setAnswers(answerService.answers(question.getId(), 1, 20, accountUser)); // 此处表示，首次显示问题列表的第一页，每页20个

        return questionVo;
    }

    @Override
    public void store(QuestionDto dto, AccountUser accountUser) {
        Date now = new Date();
        Question question = new Question();
        question.setUserId(accountUser.getUserId());
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setCategoryId(dto.getCategoryId());
        question.setCreatedAt(now);
        question.setUpdatedAt(now);

        questionMapper.insert(question);
    }

    @Override
    public void publish(Integer questionId) {
        Question question = new Question();
        Date now = new Date();
        question.setId(questionId);
        question.setUpdatedAt(now);
        question.setPublishedAt(now);
        questionMapper.updateByPrimaryKeySelective(question);

        Question publishedQuestion = questionMapper.selectByPrimaryKey(questionId);
        customEventPublisher.firePublishQuestionEvent(publishedQuestion);
    }
}