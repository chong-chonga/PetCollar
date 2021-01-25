package com.example.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author 悠一木碧
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object loginUser = session.getAttribute("loginUser");
        if(null == loginUser){
            request.setAttribute("msg", "您暂时没有权限查看, 请先登录");
            request.getRequestDispatcher("/").forward(request, response);
            return false;
        } else{
            return true;
        }
    }
}
