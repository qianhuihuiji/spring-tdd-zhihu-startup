package com.nofirst.spring.tdd.zhihu.startup.integration;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ViewQuestionsTest extends BaseContainerTest {

    @Test
    void user_can_view_questions() throws Exception {
        // given
        // 暂无需准备数据

        // when
        this.mockMvc.perform(get("/questions"))
                // then
                .andExpect(status().isOk());
    }
}