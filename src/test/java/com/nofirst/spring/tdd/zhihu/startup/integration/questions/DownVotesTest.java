package com.nofirst.spring.tdd.zhihu.startup.integration.questions;


import com.nofirst.spring.tdd.zhihu.startup.common.ResultCode;
import com.nofirst.spring.tdd.zhihu.startup.integration.BaseContainerTest;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.VoteMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Vote;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.VoteExample;
import com.nofirst.spring.tdd.zhihu.startup.model.enums.VoteActionType;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DownVotesTest extends BaseContainerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private VoteMapper voteMapper;

    @Autowired
    private QuestionService questionService;

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setupTestData() {
        VoteExample voteExample = new VoteExample();
        // 空条件，匹配所有数据，等价于delete * from vote
        voteExample.createCriteria();
        voteMapper.deleteByExample(voteExample);
    }


    @Test
    void guest_can_not_vote_down() throws Exception {
        this.mockMvc.perform(post("/questions/1/down-votes"))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void authenticated_user_can_vote_down() throws Exception {
        // given
        this.mockMvc.perform(post("/questions/1/down-votes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andVotedIdEqualTo(1);
        criteria.andResourceTypeEqualTo(Question.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_DOWN.getCode());
        List<Vote> votes = voteMapper.selectByExample(voteExample);

        assertThat(votes).size().isEqualTo(1);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void an_authenticated_user_can_cancel_vote_down() throws Exception {
        // given
        this.mockMvc.perform(post("/questions/1/down-votes"));
        VoteExample voteExample = new VoteExample();
        VoteExample.Criteria criteria = voteExample.createCriteria();
        criteria.andVotedIdEqualTo(1);
        criteria.andResourceTypeEqualTo(Question.class.getSimpleName());
        criteria.andActionTypeEqualTo(VoteActionType.VOTE_DOWN.getCode());
        long voteCount = voteMapper.countByExample(voteExample);
        assertThat(voteCount).isEqualTo(1);
        // when
        this.mockMvc.perform(delete("/questions/1/down-votes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        // then
        long voteCountAfter = voteMapper.countByExample(voteExample);
        assertThat(voteCountAfter).isEqualTo(0);
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void can_vote_down_only_once() {
        // given
        try {
            this.mockMvc.perform(post("/questions/1/down-votes"));
            this.mockMvc.perform(post("/questions/1/down-votes"));
        } catch (Exception e) {
            fail("Can not vote down twice", e);
        }
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void question_can_know_it_is_voted_down() throws Exception {
        // given
        this.mockMvc.perform(post("/questions/1/down-votes"));

        // when
        Boolean votedDown = questionService.isVotedDown(1);

        // then
        assertThat(votedDown).isTrue();
    }

    @Test
    @WithUserDetails(value = "John", userDetailsServiceBeanName = "customUserDetailsService")
    void can_know_down_votes_count() throws Exception {
        long count = questionService.downVotesCount(1);
        assertThat(count).isEqualTo(0L);
        // given when
        this.mockMvc.perform(post("/questions/1/down-votes"));

        // when
        long countAfter = questionService.downVotesCount(1);
        assertThat(countAfter).isEqualTo(1L);

    }
}
