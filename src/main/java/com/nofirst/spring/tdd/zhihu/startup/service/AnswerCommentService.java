package com.nofirst.spring.tdd.zhihu.startup.service;

import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;


public interface AnswerCommentService {

    void comment(Integer answerId, CommentDto commentDto, AccountUser accountUser);
}
