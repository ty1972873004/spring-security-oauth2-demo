package com.hxmec.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 功能描述: Spring Security 配置
 * @author  Trazen
 * @date  2020/7/8 22:42
 */
@Configuration
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }

    /**
     * 增加一个admin角色用户，一个user角色用户
     * admin----123456
     * trazen----123456
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("$2a$10$tnj.nZjSzCBckTh2fRRK9.ZTYfU0y4pDiZZChKxxeOElBsxaQCn26")
                .roles("admin")
                .and()
                .withUser("trazen")
                .password("$2a$10$tnj.nZjSzCBckTh2fRRK9.ZTYfU0y4pDiZZChKxxeOElBsxaQCn26")
                .roles("user");
    }

    /**
     * 开放oauth2授权端点
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/oauth/**").authorizeRequests()
                .antMatchers("/oauth/**").permitAll()
                .and().csrf().disable();
    }
}
