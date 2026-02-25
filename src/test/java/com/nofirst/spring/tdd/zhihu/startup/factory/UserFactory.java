package com.nofirst.spring.tdd.zhihu.startup.factory;


import com.nofirst.spring.tdd.zhihu.startup.mbg.model.User;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

import java.util.Collections;

public class UserFactory {

    public static User createUser() {
        return new User(1, "user", "password");
    }

    public static AccountUser createAccountUser() {
        User user = createUser();
        return new AccountUser(user.getId(), user.getName(), user.getPassword());
    }
}
