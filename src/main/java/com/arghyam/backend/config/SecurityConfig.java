package com.arghyam.backend.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;


@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth) throws Exception {

        KeycloakAuthenticationProvider keycloakAuthenticationProvider
                = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(
                new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(
                new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()
                .antMatchers("/login*")
                .permitAll();

        http.authorizeRequests()
                .antMatchers("/user/*")
                .hasRole("user")
                .anyRequest()
                .permitAll();

        http.authorizeRequests()
                .antMatchers("/api/v1/register*")
                .permitAll();

        http.authorizeRequests()
                .antMatchers("/api/v1/logout/*")
                .permitAll();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/api/v1/user/login");
        web.ignoring().antMatchers("/api/v1/user/*");
        web.ignoring().antMatchers("/api/v1/user/register");
        web.ignoring().antMatchers("/api/v1/validate-otp");
        web.ignoring().antMatchers("/api/v1/user/verifyOtp");
        web.ignoring().antMatchers("/api/v1/user/generate-accesstoken");
        web.ignoring().antMatchers("/user/*");
        web.ignoring().antMatchers("/api/v1/user/logout");
        web.ignoring().antMatchers("/api/v1/preference/*");
        web.ignoring().antMatchers("/api/v1/user/reset-password/*");
        web.ignoring().antMatchers("/api/v1/user/resend-verify-email/*");
        web.ignoring().antMatchers("/api/v1/user/get-profile/*");
        web.ignoring().antMatchers("/api/v1/user/update-profile/*");
        web.ignoring().antMatchers("/api/v1/user/forgot-password/*");
        web.ignoring().antMatchers("/api/v1/user/change-password");
        web.ignoring().antMatchers("/api/v1/user/scan/user-detail");
        //web.ignoring().antMatchers("/api/v1/user/update-email-id");
    }
}

