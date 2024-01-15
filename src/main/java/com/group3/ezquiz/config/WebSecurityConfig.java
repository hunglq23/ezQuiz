package com.group3.ezquiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            authz -> authz
                .requestMatchers(
                    "/vendor/**",
                    "/css/**",
                    "/images/**",
                    "/js/**",
                    "/",
                    "/login/**",
                    "/register/**",
                        "/quiz/**",
                        "/add/**")
                .permitAll())

        .formLogin(form -> form.loginPage("/login").permitAll());
    return http.build();
  }
}
