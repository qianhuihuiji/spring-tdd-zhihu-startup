package com.nofirst.spring.tdd.zhihu.startup.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotExistedException;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotPublishedException;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.CategoryMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.UserMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Category;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.CategoryExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.QuestionExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.User;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.UserExample;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.QuestionDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import com.nofirst.spring.tdd.zhihu.startup.publisher.CustomEventPublisher;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerService;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private CustomEventPublisher customEventPublisher;
    private QuestionMapper questionMapper;
    private QuestionMapperExt questionMapperExt;
    private AnswerService answerService;
    private CategoryMapper categoryMapper;
    private UserMapper userMapper;

    @Override
    public PageInfo<QuestionVo> index(Integer pageIndex, Integer pageSize, String slug, String by) {
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        criteria.andPublishedAtIsNotNull();
        if (StringUtils.isNotBlank(slug)) {
            slug(criteria, slug);
        }
        if (StringUtils.isNotBlank(by)) {
            by(criteria, by);
        }

        PageHelper.startPage(pageIndex, pageSize);
        List<Question> questions = questionMapper.selectByExample(example);
        // 如果不使用 mapper 返回的对象直接构造分页对象，total会被错误赋值成当前页的数据的数量，而不是总数
        PageInfo<Question> questionPageInfo = new PageInfo<>(questions);
        List<QuestionVo> result = new ArrayList<>();
        for (Question question : questions) {
            QuestionVo questionVo = new QuestionVo();
            questionVo.setId(question.getId());
            questionVo.setUserId(question.getUserId());
            questionVo.setTitle(question.getTitle());
            questionVo.setContent(question.getContent());
            result.add(questionVo);
        }
        PageInfo<QuestionVo> pageResult = new PageInfo<>();
        pageResult.setTotal(questionPageInfo.getTotal());
        pageResult.setPageNum(questionPageInfo.getPageNum());
        pageResult.setPageSize(questionPageInfo.getPageSize());
        pageResult.setList(result);
        return pageResult;
    }

    private void by(QuestionExample.Criteria criteria, String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        if (!CollectionUtils.isEmpty(users)) {
            User user = users.get(0);
            criteria.andUserIdEqualTo(user.getId());
        }
    }

    private void slug(QuestionExample.Criteria criteria, String slug) {
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.createCriteria().andSlugEqualTo(slug);
        List<Category> categories = categoryMapper.selectByExample(categoryExample);
        if (!CollectionUtils.isEmpty(categories)) {
            Category category = categories.get(0);
            criteria.andCategoryIdEqualTo(category.getId());
        }
    }


    @Override
    public QuestionVo show(Integer id) {
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
        questionVo.setAnswers(answerService.answers(question.getId(), 1, 20));

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
        questionMapperExt.publish(questionId, new Date());

        Question question = questionMapper.selectByPrimaryKey(questionId);
        customEventPublisher.firePublishQuestionEvent(question);
    }
}
