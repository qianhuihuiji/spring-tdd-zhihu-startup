package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.UserVo;
import com.nofirst.spring.tdd.zhihu.startup.task.ActiveUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
public class ActiveUserController {

    private final ActiveUserService activeUserService;


    @GetMapping("/active-users")
    public CommonResult<List<UserVo>> index() {
        List<UserVo> activeUsers = activeUserService.getActiveUsers();
        return CommonResult.success(activeUsers);
    }
}
