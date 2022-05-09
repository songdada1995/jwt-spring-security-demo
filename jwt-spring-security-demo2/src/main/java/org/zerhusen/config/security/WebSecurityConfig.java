package org.zerhusen.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.zerhusen.security.jwt.JWTConfigurer;
import org.zerhusen.security.jwt.JWTFilter;
import org.zerhusen.security.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    public WebSecurityConfig(
            TokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint authenticationErrorHandler,
            JwtAccessDeniedHandler jwtAccessDeniedHandler,
            JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.authenticationErrorHandler = authenticationErrorHandler;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationSuccessHandler = jwtAuthenticationSuccessHandler;
    }

    // Configure BCrypt password encoder =====================================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure paths and requests that should be ignored by Spring Security ================================

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")

                // allow anonymous resource requests
                .antMatchers(
                        "/front/**",
                        "/favicon.ico"
                );
    }

    // Configure security settings ===========================================================================

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                // 没有登录的处理
                .authenticationEntryPoint(authenticationErrorHandler)
                // 没有权限的处理
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // create no session
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/", "/index", "/loginPage").permitAll() // 无需认证

                .antMatchers("/api/authenticate").permitAll()
                // .antMatchers("/api/register").permitAll()
                // .antMatchers("/api/activate").permitAll()
                // .antMatchers("/api/account/reset-password/init").permitAll()
                // .antMatchers("/api/account/reset-password/finish").permitAll()

                .antMatchers("/api/person").hasAuthority("ROLE_USER")
                .antMatchers("/api/hidden_msg").hasAuthority("ROLE_ADMIN")

                .anyRequest().authenticated()

                .and()
                .apply(securityConfigurerAdapter());


        httpSecurity
                // 开启登录，如果没有权限，就会跳转到登录页
                .formLogin()
                // 自定义登录页，默认/login（get请求）
                .loginPage("/loginPage")
                // 登录处理地址，默认/login（post请求）
                .loginProcessingUrl("/login")
//                .usernameParameter("inputEmail") // 自定义username属性名，默认username
//                .passwordParameter("inputPassword") // 自定义password属性名，默认password
                .successHandler(jwtAuthenticationSuccessHandler);

        httpSecurity.rememberMe() // 开启记住我
                // 自定义rememberMe属性名
                .rememberMeParameter("rememberMe");

        httpSecurity
                // 开启注销
                .logout()
                // 注销处理路径，默认/logout
                .logoutUrl("/logout")
                // 注销成功后跳转路径
                .logoutSuccessUrl("/")
                // 删除cookie
                .deleteCookies(JWTFilter.AUTHORIZATION_HEADER);
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

}
