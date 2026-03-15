package com.nofirst.spring.tdd.zhihu.startup.integration.questions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.QuestionExample;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ViewQuestionsTest extends BaseContainerTest {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setupTestData() {
        QuestionExample example = new QuestionExample();
        // 空条件，匹配所有数据，等价于 delete * from question
        example.createCriteria();
        questionMapper.deleteByExample(example);
    }

    @Test
    void user_can_view_questions() throws Exception {
        // given
        // 暂无需准备数据

        // when
        this.mockMvc.perform(get("/questions"))
                // then
                .andExpect(status().isOk());
    }

    @Test
    // 下面这行代码，会在 customUserDetailsService 的 loadUserByUsername() 方法中，将 John 查出来，模拟登录
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void user_can_view_a_single_question() throws Exception {
        // given：准备测试数据
        Question question = QuestionFactory.createQuestion();
        questionMapper.insert(question);

        // when：调用接口并获取返回结果
        String jsonResponse = this.mockMvc.perform(
                        get("/questions/{id}", question.getId())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then：1. 解析 JSON 为 QuestionVo（结构不匹配时此处直接报错）
        QuestionVo questionVo = objectMapper.readValue(jsonResponse, QuestionVo.class);

        // then：2. 断言 QuestionVo 的核心字段（覆盖所有关键字段）
        assertThat(questionVo.getId()).isEqualTo(question.getId());
        assertThat(questionVo.getUserId()).isEqualTo(question.getUserId());
        assertThat(questionVo.getTitle()).isEqualTo(question.getTitle());
        assertThat(questionVo.getContent()).isEqualTo(question.getContent());
    }
}