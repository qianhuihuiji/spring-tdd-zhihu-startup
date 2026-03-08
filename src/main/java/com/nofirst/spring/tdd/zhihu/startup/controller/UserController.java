package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.UserLoginDto;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type User controller.
 */
@RestController
@RequestMapping(path = "/auth", produces = "application/json;charset=utf-8")
@AllArgsConstructor
public class UserController {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody @Validated UserLoginDto loginDTO) {
        // 1. 执行认证（用户名密码校验）
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        // 2. 认证成功，生成 Token
        AccountUser accountUser = (AccountUser) authentication.getPrincipal();
        String username = accountUser.getUsername();
        Integer userId = accountUser.getUserId();
        String token = jwtUtil.generateToken(userId, username);
        // 3. 返回 Token 给前端
        return CommonResult.success(token);
    }

    @GetMapping("/logout")
    public CommonResult<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // 退出登录
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            //清除认证
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return CommonResult.success("ok");
    }
}