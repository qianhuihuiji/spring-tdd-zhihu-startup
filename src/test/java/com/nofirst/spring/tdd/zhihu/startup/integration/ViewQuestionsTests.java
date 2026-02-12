package com.nofirst.spring.tdd.zhihu.startup.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.QuestionExample;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 添加TestInstance注解，设置为PER_CLASS模式，允许@BeforeAll使用非static方法
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ViewQuestionsTests extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @BeforeEach
    public void setupTestData() {
        QuestionExample example = new QuestionExample();
        // 空条件，匹配所有数据，等价于delete * from question
        example.createCriteria();
        questionMapper.deleteByExample(example);
    }

    @Test
    void user_can_view_questions() throws Exception {
        // given

        // when
        this.mockMvc.perform(get("/questions"))
                // then
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void user_can_view_a_published_question() throws Exception {
        // given：准备测试数据
        Question question = QuestionFactory.createQuestion();
        Date lastWeek = DateUtils.addWeeks(new Date(), -1);
        question.setPublishedAt(lastWeek);
        questionMapper.insert(question);

        // when：调用接口并获取返回结果
        String jsonResponse = this.mockMvc.perform(
                        get("/questions/{id}", question.getId())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then：1. 解析JSON为QuestionVo，用TypeReference解决泛型擦除问题，确保data字段解析为QuestionVo
        TypeReference<CommonResult<QuestionVo>> typeRef = new TypeReference<>() {
        };
        CommonResult<QuestionVo> commonResult = objectMapper.readValue(jsonResponse, typeRef);

        // then：2. 断言QuestionVo的核心字段（覆盖所有关键字段）
        assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());

        QuestionVo questionVo = commonResult.getData();
        assertThat(questionVo.getId()).isEqualTo(question.getId());
        assertThat(questionVo.getUserId()).isEqualTo(question.getUserId());
        assertThat(questionVo.getTitle()).isEqualTo(question.getTitle());
        assertThat(questionVo.getContent()).isEqualTo(question.getContent());
    }

    @Test
    void user_can_not_view_unpublished_question() throws Exception {
        // given：准备测试数据
        Question question = QuestionFactory.createQuestion();
        question.setPublishedAt(null);
        questionMapper.insert(question);

        // when:
        this.mockMvc.perform(get("/questions/{id}", question.getId()))
                // then:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.FAILED.getCode()))
                .andExpect(jsonPath("$.message").value("question not publish"));
    }

}
