package com.nofirst.spring.tdd.zhihu.startup.integration.comments;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.AnswerFactory;
import com.nofirst.spring.tdd.zhihu.startup.factory.CommentFactory;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.CommentMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.AnswerExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Comment;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.CommentExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.QuestionExample;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.CommentVo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ViewCommentsTest extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;


    @Autowired
    private QuestionMapper questionMapper;


    @Autowired
    private AnswerMapper answerMapper;


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
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

        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria();
        commentMapper.deleteByExample(commentExample);
    }


    @Test
    void can_request_all_comments_for_a_given_question() throws Exception {
        // given
        Question question = QuestionFactory.createPublishedQuestion();
        questionMapper.insert(question);
        List<Comment> comments = CommentFactory.createBatch(40, question.getId(), question.getClass().getSimpleName());
        for (Comment comment : comments) {
            commentMapper.insert(comment);
        }

        // when
        String jsonResponse = this.mockMvc.perform(get("/comments/questions/{questionId}?pageIndex=1&pageSize=20", question.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // then
        TypeReference<CommonResult<PageInfo<CommentVo>>> typeRef = new TypeReference<>() {
        };
        CommonResult<PageInfo<CommentVo>> commonResult = objectMapper.readValue(jsonResponse, typeRef);
        long code = commonResult.getCode();
        assertThat(code).isEqualTo(ResultCode.SUCCESS.getCode());

        PageInfo<CommentVo> data = commonResult.getData();

        assertThat(data.getTotal()).isEqualTo(40);
        assertThat(data.getList().size()).isEqualTo(20);
    }

    @Test
    void can_request_all_comments_for_a_given_answer() throws Exception {
        // given
        Answer answer = AnswerFactory.createAnswer(1);
        answerMapper.insert(answer);
        List<Comment> comments = CommentFactory.createBatch(40, answer.getId(), answer.getClass().getSimpleName());
        for (Comment comment : comments) {
            commentMapper.insert(comment);
        }

        // when
        String jsonResponse = this.mockMvc.perform(get("/comments/answers/{answerId}?pageIndex=1&pageSize=20", answer.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // then
        TypeReference<CommonResult<PageInfo<CommentVo>>> typeRef = new TypeReference<>() {
        };
        CommonResult<PageInfo<CommentVo>> commonResult = objectMapper.readValue(jsonResponse, typeRef);
        long code = commonResult.getCode();
        assertThat(code).isEqualTo(ResultCode.SUCCESS.getCode());

        PageInfo<CommentVo> data = commonResult.getData();

        assertThat(data.getTotal()).isEqualTo(40);
        assertThat(data.getList().size()).isEqualTo(20);
    }

}
