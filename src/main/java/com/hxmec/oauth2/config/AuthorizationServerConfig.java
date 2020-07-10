package com.hxmec.oauth2.config;

import com.hxmec.oauth2.token.HxTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 授权服务器配置
 * @author  Trazen
 * @date  2020/7/8 22:26
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * 该对象用来支持 password 模式
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /*****************InMemoryTokenStore引入 ****************************/
    /**
     *
     * 该对象用来将令牌信息存储到内存中
     * OAuth2令牌持久化主要有以下几种
     * InMemoryTokenStore  内存存储 OAuth2默认储存方式
     * JdbcTokenStore  数据库存储
     * RedisTokenStore Redis存储
     * JwkTokenStore & JwtTokenStore
     */
    @Autowired(required = false)
    private TokenStore inMemoryTokenStore;

    /**
     * 该对象将为刷新token提供支持
     *
     * InMemoryUserDetailsManager：从内存中加载用户账号 默认
     * JdbcUserDetailsManager：从数据库中加载用户账号
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /*****************redisTokenStore引入 ****************************/
    /**
     * 使用Redis存储时添加
     * 该对象用来将令牌信息存储到Redis中
     */
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;


    /*****************jwtTokenStore引入 ****************************/
    @Autowired
    @Qualifier("jwtTokenStore")
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private HxTokenEnhancer tokenEnhancer;


    /**
     * 指定密码的加密方式
     * 推荐使用BCryptPasswordEncoder, Pbkdf2PasswordEncoder, SCryptPasswordEncoder等
     * @return
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        // 使用BCrypt强哈希函数加密方案（密钥迭代次数默认为10）
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 表示支持 client_id 和 client_secret 做登录认证
        security.allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("app")
                //授权模式为password和refresh_token两种
                .authorizedGrantTypes("password", "refresh_token")
                // 配置access_token的过期时间
                .accessTokenValiditySeconds(1800)
                //配置资源id
                .resourceIds("resId")
                .scopes("all")
                //123456加密后的密码
                .secret("$2a$10$tnj.nZjSzCBckTh2fRRK9.ZTYfU0y4pDiZZChKxxeOElBsxaQCn26");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //配置令牌的存储（这里存放在内存中）
//        endpoints.tokenStore(inMemoryTokenStore)
//                .authenticationManager(authenticationManager)
//                .userDetailsService(userDetailsService);

        //配置令牌存放在Redis中
//        endpoints.tokenStore(new RedisTokenStore(redisConnectionFactory))
//                .authenticationManager(authenticationManager)
//                .userDetailsService(userDetailsService);

        //配置使用jwt存储token
        //添加自定义Token信息配置
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancerList = new ArrayList();
        enhancerList.add(jwtAccessTokenConverter);
        enhancerList.add(tokenEnhancer);
        tokenEnhancerChain.setTokenEnhancers(enhancerList);

        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenEnhancer(tokenEnhancerChain)
                .accessTokenConverter(jwtAccessTokenConverter);
    }
}
