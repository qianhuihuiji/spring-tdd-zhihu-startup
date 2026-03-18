package com.nofirst.spring.tdd.zhihu.startup.integration.answers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.factory.AnswerFactory;
import com.nofirst.spring.tdd.zhihu.startup.factory.QuestionFactory;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.AnswerMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.VoteMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.AnswerExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Vote;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.VoteExample;
import com.nofirst.spring.tdd.zhihu.startup.model.enums.VoteActionType;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.AnswerVo;
import com.nofirst.spring.tdd.zhihu.startup.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpVotesTest extends BaseContainerTest {

    @Autowired
    private VoteMapper voteMapper;
    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setupTestData() {
        VoteExample voteExample = new VoteExample();
        voteExample.createCriteria();
        voteMapper.deleteByExample(voteExample);
        AnswerExample answerExample = new AnswerExample();
        answerExample.createCriteria();
        answerMapper.deleteByExample(answerExample);
    }

    @Test
    void guest_can_not_vote_up() throws Exception {
        this.mockMvc.perform(post("/answers/1/up-votes"))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void authenticated_user_can_vote_up() throws Exception {
        // given
        this.mockMvc.perform(post("/answers/1/up-votes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andResourceIdEqualTo(1);
        criteria.andResourceTypeEqualTo(Answer.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_UP.getCode());
        List<Vote> votes = voteMapper.selectByExample(voteExample);

        assertThat(votes).size().isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void an_authenticated_user_can_cancel_vote_up() throws Exception {
        // given
        this.mockMvc.perform(post("/answers/1/up-votes"));
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andResourceIdEqualTo(1);
        criteria.andResourceTypeEqualTo(Answer.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_UP.getCode());
        long voteCount = voteMapper.countByExample(voteExample);
        assertThat(voteCount).isEqualTo(1);
        // when
        this.mockMvc.perform(delete("/answers/1/up-votes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        // then
        long voteCountAfter = voteMapper.countByExample(voteExample);
        assertThat(voteCountAfter).isEqualTo(0);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void can_vote_up_only_once() {
        // given
        try {
            this.mockMvc.perform(post("/answers/1/up-votes"));
            this.mockMvc.perform(post("/answers/1/up-votes"));
        } catch (Exception e) {
            fail("Can not vote up twice", e);
        }
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void answer_can_know_it_is_voted_up() throws Exception {
        // given
        Question publishedQuestion = QuestionFactory.createPublishedQuestion();
        questionMapper.insert(publishedQuestion);
        Answer answerWithoutVoting = AnswerFactory.createAnswer(publishedQuestion.getId());
        answerMapper.insert(answerWithoutVoting);
        Answer answerWithVoting = AnswerFactory.createAnswer(publishedQuestion.getId());
        answerMapper.insert(answerWithVoting);
        // vote up
        this.mockMvc.perform(post("/answers/{answerId}/up-votes", answerWithVoting.getId()));

        // when
        String json = this.mockMvc.perform(get("/questions/{questionId}/answers?pageIndex=1&pageSize=20", publishedQuestion.getId()))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // then
        TypeReference<CommonResult<PageInfo<AnswerVo>>> typeRef = new TypeReference<>() {
        };
        CommonResult<PageInfo<AnswerVo>> pageResult = objectMapper.readValue(json, typeRef);
        List<AnswerVo> data = pageResult.getData().getList();
        assertThat(data.size()).isEqualTo(2);

        assertThat(data.get(0).getId()).isEqualTo(answerWithoutVoting.getId());
        assertThat(data.get(0).getVoteType()).isEqualTo(VoteActionType.NOTHING.getCode());

        assertThat(data.get(1).getId()).isEqualTo(answerWithVoting.getId());
        assertThat(data.get(1).getVoteType()).isEqualTo(VoteActionType.VOTE_UP.getCode());

        // 切换到1号用户进行访问
        json = this.mockMvc.perform(get("/questions/{questionId}/answers?pageIndex=1&pageSize=20", publishedQuestion.getId())
                        .with(user(customUserDetailsService.loadUserByUsername("Jane")))
                ).andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        pageResult = objectMapper.readValue(json, typeRef);
        data = pageResult.getData().getList();
        assertThat(data.size()).isEqualTo(2);
        // 对1号用户而言，两条都是没有点过赞的
        assertThat(data.get(0).getId()).isEqualTo(answerWithoutVoting.getId());
        assertThat(data.get(0).getVoteType()).isEqualTo(VoteActionType.NOTHING.getCode());
        assertThat(data.get(1).getId()).isEqualTo(answerWithVoting.getId());
        assertThat(data.get(1).getVoteType()).isEqualTo(VoteActionType.NOTHING.getCode());
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void can_know_up_votes_count() throws Exception {
        // given
        Question publishedQuestion = QuestionFactory.createPublishedQuestion();
        questionMapper.insert(publishedQuestion);
        Answer answer = AnswerFactory.createAnswer(publishedQuestion.getId());
        answerMapper.insert(answer);
        // 2号用户 vote up
        this.mockMvc.perform(post("/answers/{answerId}/up-votes", answer.getId())).andDo(print());
        // 1号用户 vote up
        this.mockMvc.perform(post("/answers/{answerId}/up-votes", answer.getId())
                .with(user(customUserDetailsService.loadUserByUsername("Jane")))).andDo(print());

        // when
        String json = this.mockMvc.perform(get("/questions/{questionId}/answers?pageIndex=1&pageSize=20", publishedQuestion.getId()))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // then
        TypeReference<CommonResult<PageInfo<AnswerVo>>> typeRef = new TypeReference<>() {
        };
        CommonResult<PageInfo<AnswerVo>> pageResult = objectMapper.readValue(json, typeRef);
        List<AnswerVo> data = pageResult.getData().getList();
        assertThat(data.size()).isEqualTo(1);
        assertThat(data.get(0).getId()).isEqualTo(answer.getId());
        assertThat(data.get(0).getVoteUpCount()).isEqualTo(2);
    }
}