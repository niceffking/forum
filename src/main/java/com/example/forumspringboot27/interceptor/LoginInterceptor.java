package com.example.forumspringboot27.interceptor;

import com.example.forumspringboot27.config.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${public.login.url}")
    private String defaultUrl;
    /**
     * 前置处理， 预处理
     * @param request
     * @param response
     * @param handler
     * @return  true：继续主流程，false：流程中断返回false
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取session对象
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AppConfig.USER_SESSION) != null) {
            // 校验通过，已登录状态
            return true;
        }
        // 校验不通过
        // 跳转到登录页面
        if (!defaultUrl.startsWith("/")) {
            defaultUrl = '/' + defaultUrl;
        }
        response.sendRedirect(defaultUrl);
        return false;
    }
}
