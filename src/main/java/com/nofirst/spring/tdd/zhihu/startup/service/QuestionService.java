package com.nofirst.spring.tdd.zhihu.startup.service;

import com.nofirst.spring.tdd.zhihu.startup.model.dto.QuestionDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

/**
 * QuestionService
 *
 * @author nofirst
 */
public interface QuestionService {

    QuestionVo show(Integer id);

    void store(QuestionDto dto, AccountUser accountUser);

    void publish(Integer questionId);
}
