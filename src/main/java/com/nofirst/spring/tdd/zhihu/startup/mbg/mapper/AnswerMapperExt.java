package com.nofirst.spring.tdd.zhihu.startup.mbg.mapper;

import com.nofirst.spring.tdd.zhihu.startup.model.dto.UserCountDto;

import java.util.Date;
import java.util.List;

public interface AnswerMapperExt {

    List<UserCountDto> countActiveUser(Date beginTime);
}
