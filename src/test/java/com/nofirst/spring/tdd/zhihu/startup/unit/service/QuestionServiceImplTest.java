package com.nofirst.spring.tdd.zhihu.startup.unit.service;

import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotExistedException;
import com.nofirst.spring.tdd.zhihu.startup.exception.QuestionNotPublishedException;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import com.nofirst.spring.tdd.zhihu.startup.service.impl.QuestionServiceImpl;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private QuestionMapper questionMapper;

    private Question question;

    @BeforeEach
    public void setup() {
        question = QuestionFactory.createQuestion();
        Date lastWeek = DateUtils.addWeeks(new Date(), -1);
        question.setPublishedAt(lastWeek);
    }

    @Test
    void get_existed_question_by_show_method() {
        // given
        given(questionMapper.selectByPrimaryKey(1)).willReturn(this.question);

        // when
        QuestionVo existedQuestion = questionService.show(1);

        // then
        assertThat(existedQuestion).isNotNull();
        assertThat(existedQuestion.getId()).isEqualTo(this.question.getId());
        assertThat(existedQuestion.getUserId()).isEqualTo(this.question.getUserId());
        assertThat(existedQuestion.getTitle()).isEqualTo(this.question.getTitle());
        assertThat(existedQuestion.getContent()).isEqualTo(this.question.getContent());
    }

    @Test
    void get_not_existed_question_by_show_method() {
        // given
        given(questionMapper.selectByPrimaryKey(1)).willReturn(null);

        // then
        assertThatThrownBy(() -> {
            // when
            questionService.show(1);
        }).isInstanceOf(QuestionNotExistedException.class)
                .hasMessageStartingWith("question not exist");
    }

    @Test
    void get_not_published_question_by_show_method() {
        // given
        this.question.setPublishedAt(null);
        given(questionMapper.selectByPrimaryKey(1)).willReturn(this.question);

        // then
        assertThatThrownBy(() -> {
            // when
            questionService.show(1);
        }).isInstanceOf(QuestionNotPublishedException.class)
                .hasMessageStartingWith("question not publish");
    }
}