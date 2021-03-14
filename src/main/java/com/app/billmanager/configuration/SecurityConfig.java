package com.app.billmanager.configuration;

import com.app.billmanager.components.CustomUrlAuthenticationFailureHandler;
import com.app.billmanager.service.CustomOAuth2UserService;
import com.app.billmanager.service.CustomOidcUserService;
import com.app.billmanager.service.CustomUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private CustomOidcUserService oidcUserService;
    private CustomOAuth2UserService oAuth2UserService;
    private PasswordEncoder encoder;

    public SecurityConfig(CustomOidcUserService oidcUserService, CustomOAuth2UserService oAuth2UserService, PasswordEncoder encoder) {
        this.oidcUserService = oidcUserService;
        this.oAuth2UserService = oAuth2UserService;
        this.encoder = encoder;
    }

    private UriComponents defaultUri(){
        return UriComponentsBuilder.fromPath("/api/transactions")
                .queryParam("date", LocalDate.now().toString())
                .queryParam("pageNo", 1)
                .build();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }

    @Bean
    public CustomUrlAuthenticationFailureHandler failureHandler(){
        return new CustomUrlAuthenticationFailureHandler();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/oauth2/**").permitAll().antMatchers("/api**").authenticated().anyRequest().permitAll()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl(defaultUri().toUriString(), true)
                .failureHandler(failureHandler())
                .and()
                .oauth2Login().userInfoEndpoint().oidcUserService(oidcUserService).userService(oAuth2UserService)
                .and()
                .loginPage("/login").defaultSuccessUrl(defaultUri().toUriString(), true)
                .failureHandler(failureHandler())
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));

        http.cors()
                .and()
                .csrf().disable();

        http.csrf().ignoringAntMatchers("/h2-console/**")
                .and()
                .headers().frameOptions().sameOrigin();
    }

}
