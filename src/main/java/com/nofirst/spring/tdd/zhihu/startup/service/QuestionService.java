package com.nofirst.spring.tdd.zhihu.startup.service;

import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface QuestionService {

    QuestionVo show(Integer id, AccountUser accountUser);

}