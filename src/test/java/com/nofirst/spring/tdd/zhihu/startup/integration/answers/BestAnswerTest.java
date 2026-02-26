package com.nofirst.spring.tdd.zhihu.startup.integration.answers;


import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.AnswerFactory;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.AnswerExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.QuestionExample;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BestAnswerTest extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerMapper answerMapper;


    @BeforeAll
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                // 启用 spring security
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setupTestData() {
        QuestionExample questionExample = new QuestionExample();
        // 空条件，匹配所有数据，等价于delete * from question
        questionExample.createCriteria();
        questionMapper.deleteByExample(questionExample);

        AnswerExample answerExample = new AnswerExample();
        // 空条件，匹配所有数据，等价于delete * from answer
        answerExample.createCriteria();
        answerMapper.deleteByExample(answerExample);
    }

    @Test
    void guests_can_not_mark_best_answer() throws Exception {
        // 目前这个路由还不存在，但是不影响 401 的返回
        this.mockMvc.perform(post("/answers/{id}/best", 1))
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void only_the_question_creator_can_mark_a_best_answer() throws Exception {
        // given：准备测试数据
        Question questionOfOtherUser = QuestionFactory.createPublishedQuestion();
        questionOfOtherUser.setUserId(1);
        questionMapper.insert(questionOfOtherUser);
        Answer answerOfOther = AnswerFactory.createAnswer(questionOfOtherUser.getId());
        answerMapper.insert(answerOfOther);

        // when
        this.mockMvc.perform(post("/answers/{answerId}/best", answerOfOther.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().is(403));

        // given：准备测试数据
        Question questionOfJohn = QuestionFactory.createPublishedQuestion();
        questionOfJohn.setUserId(2);
        questionMapper.insert(questionOfJohn);
        Answer answerOfJohn = AnswerFactory.createAnswer(questionOfJohn.getId());
        answerMapper.insert(answerOfJohn);

        // when
        this.mockMvc.perform(post("/answers/{answerId}/best", answerOfJohn.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        // then
        Question questionAfter = questionMapper.selectByPrimaryKey(questionOfJohn.getId());
        assertThat(questionAfter.getBestAnswerId()).isEqualTo(answerOfJohn.getId());
    }
}
