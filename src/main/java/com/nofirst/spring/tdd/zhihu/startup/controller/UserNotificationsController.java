package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.NotificationVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.UserNotificationService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
public class UserNotificationsController {

    private final UserNotificationService notificationService;
    
    @GetMapping("/notifications")
    public CommonResult<PageInfo<NotificationVo>> index(@RequestParam @NotNull Integer pageIndex,
                                                        @RequestParam @NotNull Integer pageSize,
                                                        @AuthenticationPrincipal AccountUser accountUser) {
        PageInfo<NotificationVo> activeUsers = notificationService.index(accountUser.getUserId(), pageIndex, pageSize);
        return CommonResult.success(activeUsers);
    }
}
