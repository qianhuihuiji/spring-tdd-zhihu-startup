package com.nofirst.spring.tdd.zhihu.startup.integration.questions;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.NotificationMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.NotificationExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InviteUserTest extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        notificationMapper.deleteByExample(null);
    }


    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void invited_users_are_notified_when_publish_a_question() throws Exception {
        // given
        Question question = QuestionFactory.createUnpublishedQuestion();
        question.setUserId(2);
        question.setContent("@Jane please help me");
        questionMapper.insert(question);

        NotificationExample notificationExample = new NotificationExample();
        NotificationExample.Criteria criteria = notificationExample.createCriteria();
        // Jane 是1号用户，所以 notifiable_id = 1
        criteria.andNotifiableIdEqualTo(1);
        long beforeCount = notificationMapper.countByExample(notificationExample);
        assertThat(beforeCount).isEqualTo(0);
        // when
        this.mockMvc.perform(post("/questions/{questionId}/published-questions", question.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        // then
        long afterCount = notificationMapper.countByExample(notificationExample);
        assertThat(afterCount).isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void all_invited_users_are_notified() throws Exception {
        // given
        Question question = QuestionFactory.createUnpublishedQuestion();
        question.setUserId(2);
        question.setContent("@Jane @Foo please help me");
        questionMapper.insert(question);

        NotificationExample notificationExample = new NotificationExample();
        NotificationExample.Criteria criteria = notificationExample.createCriteria();
        // 查用户id为1的，也就是 Jane 
        criteria.andNotifiableIdEqualTo(1);
        long beforeCountOfJane = notificationMapper.countByExample(notificationExample);
        assertThat(beforeCountOfJane).isEqualTo(0);
        // 查用户id为3的，也就是 Foo
        notificationExample.clear();
        criteria.andNotifiableIdEqualTo(3);
        long beforeCountOfFoo = notificationMapper.countByExample(notificationExample);
        assertThat(beforeCountOfFoo).isEqualTo(0);
        // when
        this.mockMvc.perform(post("/questions/{questionId}/published-questions", question.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        // then
        // 查用户id为1的，也就是 Jane
        notificationExample.clear();
        criteria = notificationExample.createCriteria();
        criteria.andNotifiableIdEqualTo(1);
        long afterCountOfJane = notificationMapper.countByExample(notificationExample);
        assertThat(afterCountOfJane).isEqualTo(1);
        // 查用户id为3的，也就是 Foo
        notificationExample.clear();
        criteria = notificationExample.createCriteria();
        criteria.andNotifiableIdEqualTo(3);
        long afterCountOfFoo = notificationMapper.countByExample(notificationExample);
        assertThat(afterCountOfFoo).isEqualTo(1);
    }
}
