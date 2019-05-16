package com.example.shiro.shiro;


import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    private static final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    /**
     * Shiro��Web������Factory ����:shiroFilter
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //Shiro�ĺ��İ�ȫ�ӿ�,��������Ǳ����
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new PermissionsAuthorizationFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/*","anon");
        filterChainDefinitionMap.put("/login/reg","anon");
        filterChainDefinitionMap.put("/login/logout", "anon");
        filterChainDefinitionMap.put("/login/image", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/login/auth", "anon");
        filterChainDefinitionMap.put("/**","authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * ��ָ�����ֵĻ����Զ�����һ����������һ����ĸСд��bean
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        return securityManager;
    }

    /**
     * Shiro Realm �̳���AuthorizingRealm���Զ���Realm,��ָ��Shiro��֤�û���¼����Ϊ�Զ����
     */
    @Bean
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return userRealm;
    }

    /**
     * ƾ֤ƥ����
     * ���������ǵ�����У�齻��Shiro��SimpleAuthenticationInfo���д�����
     * ����������Ҫ�޸���doGetAuthenticationInfo�еĴ���;
     * ��
     * ������չƾ֤ƥ������ʵ�� ���������������������ȹ��ܣ���һ��
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //ɢ���㷨:����ʹ��MD5�㷨;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //ɢ�еĴ���������ɢ�����Σ��൱�� md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(2);
        //storedCredentialsHexEncodedĬ����true����ʱ�õ�����������õ���Hex���룻falseʱ��Base64����
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    /**
     * Shiro�������ڴ�����
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * ����Shiro��ע��(��@RequiresRoles,@RequiresPermissions),�����SpringAOPɨ��ʹ��Shiroע�����,���ڱ�Ҫʱ���а�ȫ�߼���֤
     * ������������bean(DefaultAdvisorAutoProxyCreator(��ѡ)��AuthorizationAttributeSourceAdvisor)����ʵ�ִ˹���
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }
}
