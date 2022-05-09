package org.zerhusen.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.zerhusen.security.jwt.JWTFilter;
import org.zerhusen.security.jwt.TokenProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录成功处理
 */
@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final TokenProvider tokenProvider;

    public JwtAuthenticationSuccessHandler(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        List<String> authorities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(authentication.getAuthorities())) {
            authorities.addAll(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        String token = tokenProvider.createToken(authentication, false);
        // 将token添加到header中
        response.setHeader(JWTFilter.AUTHORIZATION_HEADER, JWTFilter.TOKEN_PREFIX + token);
        // 将token添加到cookie中
        Cookie cookie = new Cookie(JWTFilter.AUTHORIZATION_HEADER, JWTFilter.TOKEN_PREFIX + token);
        cookie.setPath("/");
        cookie.setMaxAge(2 * 60 * 60);
        response.addCookie(cookie);
        log.info("登录成功，username: {}, token: {}", authentication.getName(), token);
    }
}
