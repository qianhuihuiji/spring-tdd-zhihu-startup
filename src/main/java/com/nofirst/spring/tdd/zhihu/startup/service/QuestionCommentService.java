package com.nofirst.spring.tdd.zhihu.startup.service;

import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.CommentVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;


public interface QuestionCommentService {

    void comment(Integer questionId, CommentDto commentDto, AccountUser accountUser);

    PageInfo<CommentVo> index(Integer questionId, Integer pageIndex, Integer pageSize);
}
