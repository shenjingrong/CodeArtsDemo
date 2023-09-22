package com.chinasie.codearts.security.jwt;

import org.apache.http.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import static com.chinasie.codearts.config.ApplicationConfig.*;
import static com.microx.util.HttpUtil.sendPost;


/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
public class JWTFilter extends GenericFilterBean {

    private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);

    private TokenProvider tokenProvider;

    public JWTFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String jwt = getValueFromCookie("token", request);
        if (StringUtils.hasText(jwt) && Boolean.parseBoolean(sendPost(VALIDATE_JWT_URL, jwt, null))) {
            log.info("====== Cookie jwt validate success ======");
            Authentication authentication = this.tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (ignoreUrlResource(request)) {
            log.info("====== request url：{} ======", request.getRequestURI());
        } else {
            log.info("====== no header jwt and cookie jwt ======");
            String sendRedirectUrl = request.getHeader("Referer");
            response.sendRedirect(SSO_LOGIN_URL + URLEncoder.encode(sendRedirectUrl, Consts.UTF_8.toString()));
            return;

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

      /**
     * 忽略静态资源
     *
     * @param request
     * @return
     */
    private boolean ignoreUrlResource(HttpServletRequest request) {
        if (request.getRequestURI().indexOf("/api/scheduled/") != -1
            || request.getRequestURI().indexOf("/api/wxmp/default/wx") != -1
            || request.getRequestURI().indexOf("/api/wxmp/default/wxwork") != -1
            || request.getRequestURI().indexOf("/api/wxmp/default") != -1
            || request.getRequestURI().lastIndexOf(".png") != -1
            || request.getRequestURI().lastIndexOf(".jpg") != -1
            || request.getRequestURI().lastIndexOf(".gif") != -1
            || request.getRequestURI().lastIndexOf(".mp4") != -1
            || request.getRequestURI().indexOf("/form.html") != -1
        ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从cookie中获取相应key的值
     *
     * @param key 查询key
     * @param request  请求对象
     * @return
     */
    private String getValueFromCookie(String key, HttpServletRequest request) {
        //从请求中获得cookie
        Cookie[] cookies = request.getCookies();
        String result = null;
        if (cookies != null) {
            for (Cookie ck : cookies) {
                //是否有token
                if (key.equals(ck.getName())) {
                    result = ck.getValue();
                }
            }
        }
        return result;
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
