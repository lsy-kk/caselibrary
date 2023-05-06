package com.lsykk.caselibrary.security.filter;

import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LoginService loginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (StringUtils.isBlank(token)) {
            // 如果请求头没有 token ，放行。之后的过滤器会拦截
            filterChain.doFilter(request, response);
            return;
        }
        User user = loginService.checkToken(token);
        if (user == null) {
            // token失效
            filterChain.doFilter(request, response);
            return;
        }
        // 将用户信息存入 SecurityContextHolder，便于后续查证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }
}
