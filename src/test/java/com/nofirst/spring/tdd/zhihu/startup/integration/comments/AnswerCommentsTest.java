package com.nofirst.spring.tdd.zhihu.startup.integration.comments;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.AnswerFactory;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.CommentMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.AnswerExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.CommentExample;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
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
public class AnswerCommentsTest extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private CommentMapper commentMapper;


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
        AnswerExample answerExample = new AnswerExample();
        answerExample.createCriteria();
        answerMapper.deleteByExample(answerExample);
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria();
        commentMapper.deleteByExample(commentExample);
    }


    @Test
    void guests_may_not_comment_an_answer() throws Exception {
        this.mockMvc.perform(post("/comments/answers/1"))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void signed_in_user_can_comment_an_answer() throws Exception {
        // given
        Answer answer = AnswerFactory.createAnswer(1);
        answerMapper.insert(answer);

        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andCommentedIdEqualTo(answer.getId())
                .andCommentedTypeEqualTo(Answer.class.getSimpleName());
        long beforeCount = commentMapper.countByExample(commentExample);
        assertThat(beforeCount).isEqualTo(0);

        // when
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("this is a comment");
        this.mockMvc.perform(post("/comments/answers/{answerId}", answer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                ).andDo(print())
                .andExpect(status().isOk());


        long agerCount = commentMapper.countByExample(commentExample);
        assertThat(agerCount).isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void content_is_required_to_comment_an_answer() throws Exception {
        // given
        Answer answer = AnswerFactory.createAnswer(1);
        answerMapper.insert(answer);

        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria();
        long beforeCount = commentMapper.countByExample(commentExample);
        assertThat(beforeCount).isEqualTo(0);

        // when
        CommentDto commentDto = new CommentDto();
        commentDto.setContent(null);
        this.mockMvc.perform(post("/comments/answers/{answerId}", answer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATE_FAILED.getCode()))
                .andExpect(jsonPath("$.message").value("评论内容不能为空"));
    }

}
