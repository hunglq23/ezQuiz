package com.group3.ezquiz.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable())

        .authorizeHttpRequests(
            authz -> authz
                // static resources permission
                .requestMatchers("/vendor/**", "/css/**", "/images/**", "/js/**").permitAll()
                // landing page, login page, registration end-point
                .requestMatchers("/", "/login/**", "/register/**", "/questions/**", "/option/**").permitAll()
                // other requests
                .anyRequest().authenticated())
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
                .logoutSuccessUrl("/login"));
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
