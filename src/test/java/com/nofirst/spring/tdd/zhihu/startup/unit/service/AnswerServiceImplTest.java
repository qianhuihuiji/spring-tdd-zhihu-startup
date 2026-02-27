package com.nofirst.spring.tdd.zhihu.startup.unit.service;


import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotExistedException;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotPublishedException;
import com.nofirst.spring.tdd.zhihu.startup.factory.AnswerFactory;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.matcher.AnswerMatcher;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.AnswerDto;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.impl.AnswerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceImplTest {

    @InjectMocks
    private AnswerServiceImpl answerService;

    @Mock
    private AnswerMapper answerMapper;
    @Mock
    private AnswerMapperExt answerMapperExt;
    @Mock
    private QuestionMapper questionMapper;
    @Mock
    private QuestionMapperExt questionMapperExt;

    private Answer defaultAnswer;
    private AnswerDto defaultAnswerDto;
    private List<Answer> answers;

    @BeforeEach
    public void setup() {
        this.defaultAnswer = AnswerFactory.createAnswer(1);
        this.defaultAnswerDto = AnswerFactory.createAnswerDto();
        this.answers = AnswerFactory.createAnswerBatch(10, 1);
    }

    @Test
    void can_post_an_answer_to_a_question() {
        // given
        Question question = QuestionFactory.createPublishedQuestion();
        question.setId(1);
        given(questionMapper.selectByPrimaryKey(question.getId())).willReturn(question);
        // when
        AccountUser accountUser = new AccountUser(1, "password", "username");
        answerService.store(1, this.defaultAnswerDto, accountUser);

        // then
        verify(answerMapper, times(1)).insert(argThat(new AnswerMatcher(defaultAnswer)));
    }

    @Test
    void can_not_post_an_answer_to_a_not_existed_question() {
        // given
        given(questionMapper.selectByPrimaryKey(1)).willReturn(null);

        // then
        AccountUser accountUser = new AccountUser(1, "password", "username");
        assertThatThrownBy(() -> {
            // when
            answerService.store(1, this.defaultAnswerDto, accountUser);
        }).isInstanceOf(QuestionNotExistedException.class)
                .hasMessageStartingWith("question not exist");
    }

    @Test
    void can_not_post_an_answer_to_a_not_published_question() {
        // given
        Question question = QuestionFactory.createUnpublishedQuestion();
        question.setId(1);
        given(questionMapper.selectByPrimaryKey(question.getId())).willReturn(question);

        // then
        AccountUser accountUser = new AccountUser(1, "password", "username");
        assertThatThrownBy(() -> {
            // when
            answerService.store(1, this.defaultAnswerDto, accountUser);
        }).isInstanceOf(QuestionNotPublishedException.class)
                .hasMessageStartingWith("question not publish");
    }

    @Test
    void can_mark_one_answer_as_the_best() {
        // given
        Question publishedQuestion = QuestionFactory.createPublishedQuestion();
        publishedQuestion.setId(1);
        Answer answer = AnswerFactory.createAnswer(publishedQuestion.getId());
        publishedQuestion.setBestAnswerId(answer.getId());
        given(answerMapper.selectByPrimaryKey(answer.getId())).willReturn(answer);

        // when
        answerService.markAsBest(1);

        // then
        verify(questionMapperExt, times(1)).markAsBestAnswer(publishedQuestion.getId(), answer.getId());
    }

    @Test
    void can_delete_answer() {
        // given

        // when
        answerService.destroy(1);

        // then
        verify(answerMapper, times(1)).deleteByPrimaryKey(1);
    }

    @Test
    void a_question_has_many_answers() {
        // given
        given(answerMapperExt.selectByQuestionId(1)).willReturn(this.answers);

        // when
        PageInfo<Answer> answersPage = answerService.answers(1, 1, 20);

        // then
        assertThat(answersPage.getTotal()).isEqualTo(10);
        assertThat(answersPage.getSize()).isEqualTo(10);
    }
}