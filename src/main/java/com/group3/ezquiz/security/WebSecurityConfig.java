package com.group3.ezquiz.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.group3.ezquiz.security.oauth2.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final CustomOAuth2UserService oAuth2UserService;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable())

        .authorizeHttpRequests(
            authz -> authz
                // static resources permission
                .requestMatchers("/vendor/**", "/css/**", "/images/**", "/js/**").permitAll()
                // landing page, login page, registration end-point
                .requestMatchers("/", "/login/**", "/register/**").permitAll()
                // authentication end-point
                .requestMatchers("/forgot-password", "/send-forgot-password",
                    "/reset-forgot-password", "/update-forgot-password", "/verify-account")
                .permitAll()
                // other requests
                .anyRequest().authenticated())

        // OAuth2 login config
        .oauth2Login(
            oauth2Login -> oauth2Login
                .loginPage("/login")
                .userInfoEndpoint(
                    userInfo -> userInfo.userService(oAuth2UserService))
                .defaultSuccessUrl("/home", true))

        // login form config
        .formLogin(
            form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll())
        // logout config
        .logout(
            logout -> logout
                .logoutRequestMatcher(
                    new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout"));
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationManager authenticationManager(
      HttpSecurity http,
      AuthenticationProvider authProvider) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http
        .getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.authenticationProvider(authProvider);
    return authenticationManagerBuilder.build();
  }

}
