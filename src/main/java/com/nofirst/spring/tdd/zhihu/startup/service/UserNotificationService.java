package com.nofirst.spring.tdd.zhihu.startup.service;

import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.NotificationVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;

public interface UserNotificationService {

    PageInfo<NotificationVo> index(Integer userId, Integer pageIndex, Integer pageSize, AccountUser accountUser);
}