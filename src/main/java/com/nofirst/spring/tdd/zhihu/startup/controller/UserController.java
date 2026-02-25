package com.nofirst.spring.tdd.zhihu.startup.controller;

import com.nofirst.spring.tdd.zhihu.startup.common.CommonResult;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.UserLoginDto;
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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        // 从数据库查询用户ID（实际开发中可优化，避免重复查询）
        Long userId = 1L; // 示例，实际需从用户信息中获取
        String token = jwtUtil.generateToken(userId, username, role);
        // 3. 返回 Token 给前端
        return CommonResult.success(token);
    }

    /**
     * Logout common result.
     *
     * @param request  the request
     * @param response the response
     * @return the common result
     */
//@PreAuthorize配合@EnableGlobalMethodSecurity(prePostEnabled = true)使用
    //@PreAuthorize("hasAuthority('/user/logout')")
    @GetMapping("/logout")
    public CommonResult logout(HttpServletRequest request, HttpServletResponse response) {
        // 退出登录
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            //清除认证
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return CommonResult.success(null);
    }
}