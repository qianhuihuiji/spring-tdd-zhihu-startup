package com.nofirst.spring.tdd.zhihu.startup.integration;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
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
import com.nofirst.spring.tdd.zhihu.startup.model.vo.NotificationVo;
import com.nofirst.spring.tdd.zhihu.startup.security.CustomUserDetailsService;
import org.assertj.core.api.Assertions;
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

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


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

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void a_user_can_fetch_their_unread_notifications() throws Exception {
        // given
        Question question = QuestionFactory.createPublishedQuestion();
        question.setUserId(1);
        questionMapper.insert(question);
        // 1号用户（Jane）订阅了一个问题，如果有人发表了答案，Jane 会收到一条通知
        Subscription subscription = SubscriptionFactory.createSubscription(1, question.getId());
        subscriptionMapper.insert(subscription);
        AnswerDto answer = AnswerFactory.createAnswerDto();
        this.mockMvc.perform(post("/questions/{id}/answers", question.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answer))
        );


        // when：模拟 Jane 用户登录之后的行为
        String jsonResponse = this.mockMvc.perform(get("/notifications?pageIndex=1&pageSize=10")
                        .with(user(customUserDetailsService.loadUserByUsername("Jane")))
                ).andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // then
        TypeReference<CommonResult<PageInfo<NotificationVo>>> typeRef = new TypeReference<>() {
        };
        CommonResult<PageInfo<NotificationVo>> commonResult = objectMapper.readValue(jsonResponse, typeRef);
        long code = commonResult.getCode();
        Assertions.assertThat(code).isEqualTo(ResultCode.SUCCESS.getCode());

        PageInfo<NotificationVo> data = commonResult.getData();
        assertThat(data.getTotal()).isEqualTo(1);
        assertThat(data.getList().size()).isEqualTo(1);
    }

    @Test
    void guests_can_not_see_unread_notifications() throws Exception {
        this.mockMvc.perform(get("/notifications"))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void clear_all_unread_notifications_after_see_unread_notifications_page() throws Exception {
        // given
        Question question = QuestionFactory.createPublishedQuestion();
        question.setUserId(1);
        questionMapper.insert(question);
        // 1号用户订阅了一个问题，如果有人发表了答案，他会收到一条通知
        Subscription subscription = SubscriptionFactory.createSubscription(1, question.getId());
        subscriptionMapper.insert(subscription);

        // 初始的未读通知数量是0
        NotificationExample example = new NotificationExample();
        example.createCriteria().andNotifiableIdEqualTo(1).andReadAtIsNull();
        long beforeCount = notificationMapper.countByExample(example);
        assertThat(beforeCount).isEqualTo(0);

        AnswerDto answer = AnswerFactory.createAnswerDto();
        this.mockMvc.perform(post("/questions/{id}/answers", question.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answer))
        );
        // 未读通知数量变成1
        long afterCount = notificationMapper.countByExample(example);
        assertThat(afterCount).isEqualTo(1);

        // when
        // 切换到1号用户进行访问
        this.mockMvc.perform(get("/notifications?pageIndex=1&pageSize=10")
                .with(user(customUserDetailsService.loadUserByUsername("Jane")))
        ).andExpect(status().isOk()).andReturn();

        // then
        // 最终未读通知数量变成0
        long finalCount = notificationMapper.countByExample(example);
        assertThat(finalCount).isEqualTo(0);
    }
}
