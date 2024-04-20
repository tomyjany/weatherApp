package com.inn.weatherApp.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtRequestFilter jwtRequestFilter = jwtRequestFilter();
        http
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/subscriber-only/**").hasAuthority("ROLE_SUBSCRIBED")
                        .requestMatchers("/user/signup").permitAll()
                        .requestMatchers("/user/signin").permitAll()
                        .requestMatchers("/user/test").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(customerDetailsService, jwtUtil);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customerDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}

/*
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Bean
    public JwtRequestFilter jwtRequestFilter(JWTUtil jwtUtil) {
        return new JwtRequestFilter(jwtUtil);  // Pass the JWTUtil as a constructor argument
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .authorizeHttpRequests((authz) -> authz
//
//                        .requestMatchers("/subscriber-only/**").hasAuthority("ROLE_SUBSCRIBED") // Only subscribed users
//                        .requestMatchers("/user/signup").permitAll() // Public endpoints
//                        .requestMatchers("/user/signin").permitAll() // Public endpoints
//                        .requestMatchers("/public/**").permitAll() // Public endpoints
//
//                        .anyRequest().authenticated() // All other endpoints require authentication
//                )
//                .userDetailsService(customerDetailsService)
//                .csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(Customizer.withDefaults()); // This is just an example, you can configure JWT or formLogin here
//
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                // Existing configurations
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/subscriber-only/**").hasAuthority("ROLE_SUBSCRIBED")
                        .requestMatchers("/user/signup").permitAll()
                        .requestMatchers("/user/signin").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customerDetailsService)
                .passwordEncoder(com.inn.weatherApp.JWT.PasswordConfig.passwordEncoder());
    }

}



*/