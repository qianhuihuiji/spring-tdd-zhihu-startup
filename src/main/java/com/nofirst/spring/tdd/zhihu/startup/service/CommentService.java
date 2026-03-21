package com.nofirst.spring.tdd.zhihu.startup.service;

import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.CommentVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface CommentService {

    void comment(Integer commentedId, String commentedType, CommentDto commentDto, AccountUser accountUser);

    PageInfo<CommentVo> index(Integer commentedId, String commentedType, Integer pageIndex, Integer pageSize, AccountUser accountUser);
}