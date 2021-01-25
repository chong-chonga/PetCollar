package com.example.nativecomponent;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author Lexin Huang
 * /* 是Servlet的写法, /** 是Spring的写法
 */
@Slf4j
@WebFilter(urlPatterns = {"/css/*", "/js/*"})
public class MyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Filter 初始化完成");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("filter过滤到请求了");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
