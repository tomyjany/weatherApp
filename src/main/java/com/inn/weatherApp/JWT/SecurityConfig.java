package com.inn.weatherApp.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz

                        .requestMatchers("/subscriber-only/**").hasAuthority("ROLE_SUBSCRIBED") // Only subscribed users
                        .requestMatchers("/user/signup").permitAll() // Public endpoints
                        .requestMatchers("/user/signin").permitAll() // Public endpoints
                        .requestMatchers("/public/**").permitAll() // Public endpoints

                        .anyRequest().authenticated() // All other endpoints require authentication
                )
                .userDetailsService(customerDetailsService)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults()); // This is just an example, you can configure JWT or formLogin here
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customerDetailsService)
                .passwordEncoder(com.inn.weatherApp.JWT.PasswordConfig.passwordEncoder());
    }

}



