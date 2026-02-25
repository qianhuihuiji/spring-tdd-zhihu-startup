package com.nofirst.spring.tdd.zhihu.startup.mbg.mapper;


import com.nofirst.spring.tdd.zhihu.startup.mbg.model.User;


public interface UserMapperExt {

    User selectByUsername(String username);
}
