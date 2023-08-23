package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jsc
 * @version 1.0
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    private static final AntPathMatcher matcher = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String uri = httpServletRequest.getRequestURI();
        log.info("拦截URL请求:{}",uri);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        boolean checkUri = checkUri(urls, uri);
        if (checkUri) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
//后台管理系统
        if (httpServletRequest.getSession().getAttribute("employee") != null) {
            Long employeeId = (Long)httpServletRequest.getSession().getAttribute("employee");
            log.info("用户已登录，用户id为:{}",httpServletRequest.getSession().getAttribute("employee"));
            BaseContext.setThreadLocal(employeeId);
            log.info("线程id: {}",Thread.currentThread().getId());
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
//移动端系统
        if (httpServletRequest.getSession().getAttribute("user") != null) {
            Long userId = (Long)httpServletRequest.getSession().getAttribute("user");
            log.info("用户已登录，用户id为:{}",httpServletRequest.getSession().getAttribute("user"));
            BaseContext.setThreadLocal(userId);
            log.info("线程id: {}",Thread.currentThread().getId());
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean checkUri(String[] urls,String uri) {
        for (String url : urls) {
            if (matcher.match(url,uri)) {
                return true;
            }
        }
        return false;
    }
}
