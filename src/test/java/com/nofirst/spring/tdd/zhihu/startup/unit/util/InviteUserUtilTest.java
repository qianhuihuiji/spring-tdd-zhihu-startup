package com.nofirst.spring.tdd.zhihu.startup.unit.util;

import com.nofirst.spring.tdd.zhihu.startup.util.InviteUserUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class InviteUserUtilTest {

    @Test
    void extract_invited_user_from_content() {
        // given
        String content = "@Jane @Foo look at this";

        // when
        List<String> users = InviteUserUtil.extractInvitedUser(content);

        // then
        assertThat(users).containsExactly("Jane", "Foo");
    }

    @Test
    void extract_invited_user_with_single_user() {
        // given
        String content = "@John please check this";

        // when
        List<String> users = InviteUserUtil.extractInvitedUser(content);

        // then
        assertThat(users).containsExactly("John");
    }

    @Test
    void extract_invited_user_with_no_user() {
        // given
        String content = "This is a normal comment without mention";

        // when
        List<String> users = InviteUserUtil.extractInvitedUser(content);

        // then
        assertThat(users).isEmpty();
    }

    @Test
    void extract_invited_user_with_at_in_middle() {
        // given
        String content = "Hello @Alice how are you @Bob";

        // when
        List<String> users = InviteUserUtil.extractInvitedUser(content);

        // then
        assertThat(users).containsExactly("Alice", "Bob");
    }

    @Test
    void extract_invited_user_with_empty_content() {
        // given
        String content = "";

        // when
        List<String> users = InviteUserUtil.extractInvitedUser(content);

        // then
        assertThat(users).isEmpty();
    }

    @Test
    void extract_invited_user_with_username_containing_numbers() {
        // given
        String content = "@User123 @Test456 hello";

        // when
        List<String> users = InviteUserUtil.extractInvitedUser(content);

        // then
        assertThat(users).containsExactly("User123", "Test456");
    }

    @Test
    void extract_invited_user_with_username_containing_underscore() {
        // given
        String content = "@user_name @test_user hello";

        // when
        List<String> users = InviteUserUtil.extractInvitedUser(content);

        // then
        assertThat(users).containsExactly("user_name", "test_user");
    }

    @Test
    void extract_invited_user_at_end_of_content() {
        // given
        String content = "check this out @Charlie";

        // when
        List<String> users = InviteUserUtil.extractInvitedUser(content);

        // then
        assertThat(users).containsExactly("Charlie");
    }
}
