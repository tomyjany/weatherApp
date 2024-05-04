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
import org.springframework.web.client.RestTemplate;

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
        /*
        http
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/subscriber-only/**").hasAuthority("ROLE_SUBSCRIBED")
                        .requestMatchers("/user/signup").permitAll()
                        .requestMatchers("/user/signin").permitAll()
                        .requestMatchers("/user/test").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/signup").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/*.js").permitAll()
                        .requestMatchers("/*.css").permitAll()
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated())
                //.csrf(AbstractHttpConfigurer::disable);
                .csrf().disable();
                //.httpBasic(Customizer.withDefaults());
        return http.build();



         */
        http
                .csrf().disable()  // If CSRF is not needed, ensure it's disabled
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/*.js", "/*.css", "/*.ico", "/index.html").permitAll()  // Static resources
                        .requestMatchers("/api/subscriber-only/**").hasAuthority("ROLE_SUBSCRIBED")
                        .requestMatchers("/api/user/pay").hasAuthority("ROLE_UNSUBSCRIBED")
                        .requestMatchers("/**").permitAll())  // Allow all other requests
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
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
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}