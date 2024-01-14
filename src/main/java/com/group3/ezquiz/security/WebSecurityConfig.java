package com.group3.ezquiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.group3.ezquiz.model.Role;

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
                    "/register/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/home")
                .hasRole(Role.LEARNER.toString()))

        .formLogin(
            form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                // .successForwardUrl("/home")
                .permitAll())
        .logout(
            logout -> logout
                .logoutRequestMatcher(
                    new AntPathRequestMatcher("/logout"))
                .permitAll());
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
