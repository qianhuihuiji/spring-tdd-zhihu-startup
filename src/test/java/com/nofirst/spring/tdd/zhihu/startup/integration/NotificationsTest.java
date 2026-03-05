package com.nofirst.spring.tdd.zhihu.startup.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.factory.AnswerFactory;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.factory.SubscriptionFactory;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.NotificationMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.SubscriptionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.NotificationExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Subscription;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.AnswerDto;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificationsTest extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setupTestData() {
        NotificationExample example = new NotificationExample();
        // 空条件，匹配所有数据，等价于delete * from notification
        example.createCriteria();
        notificationMapper.deleteByExample(example);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void a_notification_is_prepared_when_a_subscribed_question_receives_a_new_answer_by_other_people() throws Exception {
        // given
        Question question = QuestionFactory.createPublishedQuestion();
        question.setUserId(1);
        questionMapper.insert(question);

        Subscription subscription = SubscriptionFactory.createSubscription(1, question.getId());
        subscriptionMapper.insert(subscription);

        NotificationExample notificationExample = new NotificationExample();
        NotificationExample.Criteria criteria = notificationExample.createCriteria();
        // 用户id为 1 的通知
        criteria.andNotifiableIdEqualTo(1);
        long beforeCount = notificationMapper.countByExample(notificationExample);
        assertThat(beforeCount).isEqualTo(0);
        // when
        AnswerDto answer = AnswerFactory.createAnswerDto();
        this.mockMvc.perform(post("/questions/{id}/answers", question.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answer))
        );

        // then
        long afterCount = notificationMapper.countByExample(notificationExample);
        assertThat(afterCount).isEqualTo(1);
    }
}
