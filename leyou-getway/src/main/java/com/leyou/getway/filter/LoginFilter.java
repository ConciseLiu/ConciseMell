package com.leyou.getway.filter;

import com.leyou.auth.utils.CookieUtils;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.getway.config.FilterProperties;
import com.leyou.getway.config.JwtProperties;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {
    @Autowired
    private JwtProperties properties;

    @Autowired
    private FilterProperties filterProperties;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        // 过滤可以通过的请求
        for (String allowPath : this.filterProperties.getAllowPaths()) {
            if (requestURI.startsWith(allowPath)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = CookieUtils.getCookieValue(request, properties.getCookieName());
        // 校验
        try {
            // 校验通过什么都不做，即放行
            JwtUtils.getInfoFromToken(token, this.properties.getPublicKey());
        } catch (Exception e) {
            // 校验出现异常，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
            logger.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e );
        }
        return null;
    }
}
