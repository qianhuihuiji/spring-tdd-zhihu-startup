package com.nofirst.spring.tdd.zhihu.startup.integration;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.AnswerFactory;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.AnswerExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.QuestionExample;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.UserVo;
import com.nofirst.spring.tdd.zhihu.startup.redis.JsonRedisTemplate;
import com.nofirst.spring.tdd.zhihu.startup.task.ActiveUserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActiveUserTest extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private JsonRedisTemplate jsonRedisTemplate;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerMapper answerMapper;

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
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria();
        questionMapper.deleteByExample(questionExample);
        AnswerExample answerExample = new AnswerExample();
        answerExample.createCriteria();
        answerMapper.deleteByExample(answerExample);
    }

    @Test
    void can_calculate_active_user() {
        jsonRedisTemplate.delete(ActiveUserService.CACHE_KEY);
        // given
        // John 创建了 1 个 Question，得 4 分
        Question question = QuestionFactory.createPublishedQuestion();
        question.setUserId(2);
        questionMapper.insert(question);
        // Jane 创建了 1 个 Answer，得 1 分
        Answer answer = AnswerFactory.createAnswer(question.getId());
        answer.setUserId(1);
        answerMapper.insert(answer);
        // 还有一个用户 Foo，不得分

        // when
        activeUserService.calculateAndCacheActiveUsers();

        // then
        List<UserVo> userVos = (List<UserVo>) jsonRedisTemplate.opsForValue().get(ActiveUserService.CACHE_KEY);
        assertThat(userVos).isNotNull();
        assertThat(userVos.size()).isEqualTo(2);
        assertThat(userVos.stream().map(UserVo::getId).collect(Collectors.toList())).isEqualTo(Arrays.asList(2, 1));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void can_get_all_active_user() throws Exception {
        // 先清除数据，避免脏数据干扰
        jsonRedisTemplate.delete(ActiveUserService.CACHE_KEY);
        // given
        // John 创建了 1 个 Question，得 4 分
        Question question = QuestionFactory.createPublishedQuestion();
        question.setUserId(2);
        questionMapper.insert(question);
        // Jane 创建了 1 个 Answer，得 1 分
        Answer answer = AnswerFactory.createAnswer(question.getId());
        answer.setUserId(1);
        answerMapper.insert(answer);
        // 还有一个用户 Foo，不得分

        // when
        String jsonResponse = this.mockMvc.perform(get("/active-users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // then
        TypeReference<CommonResult<List<UserVo>>> typeRef = new TypeReference<>() {
        };
        CommonResult<List<UserVo>> commonResult = objectMapper.readValue(jsonResponse, typeRef);
        long code = commonResult.getCode();
        assertThat(code).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(commonResult.getData().size()).isEqualTo(2);
        assertThat(commonResult.getData().stream().map(UserVo::getId).collect(Collectors.toList())).isEqualTo(Arrays.asList(2, 1));
    }
}
