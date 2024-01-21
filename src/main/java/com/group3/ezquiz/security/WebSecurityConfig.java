package com.group3.ezquiz.security;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.group3.ezquiz.security.oauth2.CustomOAuth2UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                .requestMatchers("/", "/login/**", "/register/**", "/questions/**", "/options/**").permitAll()
                // for testing end-point
                // .requestMatchers("").permitAll()
                // other requests
                .anyRequest().authenticated())

        // OAuth2 login config
        .oauth2Login(
            oauth2Login -> oauth2Login
                .loginPage("/login")
                .userInfoEndpoint(
                    userInfo -> userInfo.userService(oAuth2UserService))
                .successHandler(getHandler()))

        // login form config
        .formLogin(
            form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/profile", true)
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

  private AuthenticationSuccessHandler getHandler() {
    return new AuthenticationSuccessHandler() {
      @Override
      public void onAuthenticationSuccess(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication) throws IOException, ServletException {
        // CustomOAuth2User oauthUser = (CustomOAuth2User)
        // authentication.getPrincipal();
        // userService.processOAuthPostLogin(oauthUser.getEmail());
        response.sendRedirect("/profile");
      }
    };
  }

}
