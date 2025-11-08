package com.ecommerce.ecommerce_site.config;

import com.ecommerce.ecommerce_site.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config =  new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        //only allow request that come from following paths
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/login", "/logout", "/send-otp", "/validate-otp","/shop","/uploads/**", "/product/**", "/api/**", "/shop/cart").permitAll()
                        //for other request require user login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/")                 // login view on /login (i have disabeld the view ) it will work for post method only
                        .loginProcessingUrl("/login")       // form action="/login"
                        .usernameParameter("email")
                        .defaultSuccessUrl("/?login=true", true)
                        .failureUrl("/?error=true")          // redirect to "/" if login fails
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // /logout to logout user
                        .logoutSuccessUrl("/?logout=true") // if logout is successfull then goto home page
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/?login=true", true)
                )
                .rememberMe(remember -> remember
                        .key("bc3a91d5-9c4e-42d0-bf56-02f61a15f1e9")      // important: set a unique key
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .useSecureCookie(true)
                        .alwaysRemember(true)
                );

        return http.build();
    }


    //creating custom AuthenticationProvider to authenticate user from database
    @Bean
    public AuthenticationProvider authenticationProvider(){
        // AuthenticationProvider is an interface so we can't create object of it
        // so we are using class DaoAuthenticationProvider which implements AuthenticationProvider
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
