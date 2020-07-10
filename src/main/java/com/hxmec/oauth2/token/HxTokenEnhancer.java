package com.hxmec.oauth2.token;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 功能描述: Token增强
 * @author  Trazen
 * @date  2020/7/10 16:29
 */
@Component
public class HxTokenEnhancer implements TokenEnhancer {

    private final static String CLIENT_CREDENTIALS = "client_credentials";


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        /**
         * 客户端模式不修改
         */
        if (CLIENT_CREDENTIALS.equals(authentication.getOAuth2Request().getGrantType())) {
            return accessToken;
        }

        final Map<String, Object> additionalInfo = new HashMap<>(8);
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("author", "Trazen");
        info.put("email", "trazen@126.com");
        info.put("GitHub", "https://github.com/ty1972873004/spring-security-oauth2-demo");
        info.put("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        additionalInfo.put("info", info);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
