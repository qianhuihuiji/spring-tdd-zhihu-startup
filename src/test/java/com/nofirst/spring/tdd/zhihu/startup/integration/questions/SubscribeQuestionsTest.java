package com.nofirst.spring.tdd.zhihu.startup.integration.questions;


import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.SubscriptionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.SubscriptionExample;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubscribeQuestionsTest extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Autowired
    private QuestionMapper questionMapper;


    @BeforeAll
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setupTestData() {
        SubscriptionExample example = new SubscriptionExample();
        // 空条件，匹配所有数据，等价于delete * from question
        example.createCriteria();
        subscriptionMapper.deleteByExample(example);
    }


    @Test
    void guests_may_not_subscribe_to_or_unsubscribe_from_questions() throws Exception {
        this.mockMvc.perform(post("/questions/subscriptions"))
                .andDo(print())
                .andExpect(status().is(401));

        this.mockMvc.perform(delete("/questions/subscriptions"))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void a_user_can_subscribe_to_questions() throws Exception {
        // given
        Question question = QuestionFactory.createPublishedQuestion();
        questionMapper.insert(question);

        SubscriptionExample example = new SubscriptionExample();
        SubscriptionExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(2);
        criteria.andQuestionIdEqualTo(question.getId());
        long beforeCount = subscriptionMapper.countByExample(example);
        assertThat(beforeCount).isEqualTo(0);
        // when
        this.mockMvc.perform(post("/questions/{questionId}/subscriptions", question.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        // then
        long afterCount = subscriptionMapper.countByExample(example);
        assertThat(afterCount).isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void a_user_can_unsubscribe_from_questions() throws Exception {
        // given
        Question question = QuestionFactory.createPublishedQuestion();
        questionMapper.insert(question);

        // when
        // 下面的逻辑在上一个测试中已经验证过了
        this.mockMvc.perform(post("/questions/{questionId}/subscriptions", question.getId()));
        this.mockMvc.perform(delete("/questions/{questionId}/subscriptions", question.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        // then
        SubscriptionExample example = new SubscriptionExample();
        SubscriptionExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(2);
        criteria.andQuestionIdEqualTo(question.getId());
        long count = subscriptionMapper.countByExample(example);
        assertThat(count).isEqualTo(0);
    }

}
