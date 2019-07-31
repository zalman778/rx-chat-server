package com.hwx.rx_chat_server.config;


import com.hwx.rx_chat_server.filters.jwt.JWTAuthenticationFilter;
import com.hwx.rx_chat_server.filters.jwt.JWTLoginFilter;
import com.hwx.rx_chat_server.service.jwt.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(SecurityAuthProperties.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityAuthProperties properties;

    @Autowired
    UserDetailsService userStaticRepository;

    @Autowired
    TokenAuthenticationService tokenAuthenticationService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //session managment
        http    .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .sessionFixation().migrateSession();
        //authorization
        http
                .csrf().disable().authorizeRequests()
                // No need authentication.
                .antMatchers("/").permitAll() //
                .antMatchers("/admin").permitAll() //
                .antMatchers(HttpMethod.POST, "/api/signup").permitAll() //
                .antMatchers(HttpMethod.POST, "/api/login").permitAll() //
                .antMatchers(HttpMethod.GET, "/api/login").permitAll() // For Test on Browser
                // Need authentication.
                .anyRequest().authenticated()
                //
                .and()
                //
                // Add Filter 1 - JWTLoginFilter
                //
                .addFilterBefore(new JWTLoginFilter("/api/login", authenticationManager(), tokenAuthenticationService),
                        UsernamePasswordAuthenticationFilter.class)
                //
                // Add Filter 2 - JWTAuthenticationFilter
                //
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    /**
     * Configures the security provider with an in-memory authenticator manager.
     *
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userStaticRepository);
    }
}