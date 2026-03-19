package com.nofirst.spring.tdd.zhihu.startup.integration.questions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.QuestionExample;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.QuestionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateQuestionsTest extends BaseContainerTest {

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
    void guests_may_not_create_questions() throws Exception {
        this.mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void an_authenticated_user_can_create_new_questions() throws Exception {
        // given
        QuestionDto questionDto = QuestionFactory.createQuestionDto();
        QuestionExample example = new QuestionExample();
        example.createCriteria();
        long beforeCount = questionMapper.countByExample(example);

        // when
        this.mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        // then
        long afterCount = questionMapper.countByExample(example);
        // 调用之后 questionDto 增加了 1 条
        assertThat(afterCount - beforeCount).isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void title_is_required() throws Exception {
        // given
        QuestionDto questionDto = QuestionFactory.createQuestionDto();
        questionDto.setTitle("");

        // when
        this.mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATE_FAILED.getCode()))
                .andExpect(jsonPath("$.message").value("标题不能为空"));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void content_is_required() throws Exception {
        // given
        QuestionDto questionDto = QuestionFactory.createQuestionDto();
        questionDto.setContent("");

        // when
        this.mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATE_FAILED.getCode()))
                .andExpect(jsonPath("$.message").value("内容不能为空"));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void category_id_is_required() throws Exception {
        // given
        QuestionDto questionDto = QuestionFactory.createQuestionDto();
        questionDto.setCategoryId(null);

        // when
        this.mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATE_FAILED.getCode()))
                .andExpect(jsonPath("$.message").value("问题分类不能为空"));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void category_id_is_valid() throws Exception {
        // given
        QuestionDto questionDto = QuestionFactory.createQuestionDto();
        // -1 的分类在正常情况下是不会存在的
        questionDto.setCategoryId(-1);

        // when
        this.mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDto)))
                // then
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATE_FAILED.getCode()))
                .andExpect(jsonPath("$.message").value("问题分类不存在"));
    }
}