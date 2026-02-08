package com.nofirst.spring.tdd.zhihu.startup.service;

import com.nofirst.spring.tdd.zhihu.startup.model.vo.QuestionVo;

/**
 * QuestionService
 *
 * @author nofirst
 */
public interface QuestionService {
    
    QuestionVo show(Integer id);

}
