package com.cafegalactica.oauth_demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/error").permitAll() // allow home and error pages
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated() // all other requests require auth)
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userAuthoritiesMapper(this.userAuthoritiesMapper())));
        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if(authority instanceof OidcUserAuthority oidcUserAuthority) {
                    var claims = oidcUserAuthority.getIdToken().getClaims();
                    var isAdmin = claims.get("admin") instanceof Boolean && (Boolean) claims.get("admin");
                    if(isAdmin) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                }
            });
            mappedAuthorities.addAll(authorities);
            mappedAuthorities.add(new SimpleGrantedAuthority(("ROLE_USER")));
            return mappedAuthorities;
        });
    }
}
