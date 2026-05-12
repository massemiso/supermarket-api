package com.massemiso.supermarket_api.config;

import com.massemiso.supermarket_api.config.filter.JwtTokenValidator;
import com.massemiso.supermarket_api.exception.GlobalHandlerException;
import com.massemiso.supermarket_api.service.UserService;
import com.massemiso.supermarket_api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private JwtUtil jwtUtil;

  @Bean
  public SecurityFilterChain securityFilterChain
      (HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
//        .httpBasic(Customizer.withDefaults())
        .sessionManagement(
            session
                -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new GlobalHandlerException()))
        .authorizeHttpRequests(auth -> {
          // public
//          auth.requestMatchers(HttpMethod.GET,"/api/branches/**").permitAll();
//          auth.requestMatchers(HttpMethod.GET,"/api/products/**").permitAll();
//          auth.requestMatchers(HttpMethod.GET,"/api/sales/**").permitAll();

          // auth infrastructure
          auth.requestMatchers("/api/auth/**", "/h2-console/**").permitAll();

          // everything else be authenticated by default
          auth.anyRequest().authenticated();
        })
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin)
        )
        .addFilterBefore(
            new JwtTokenValidator(jwtUtil),
            BasicAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(
      PasswordEncoder passwordEncoder,
      UserService userService){
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(
        userService
    );
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

}
