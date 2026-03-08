package com.nofirst.spring.tdd.zhihu.startup.service;

import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.NotificationVo;


public interface UserNotificationService {

    PageInfo<NotificationVo> index(Integer userId, Integer pageIndex, Integer pageSize);
}
