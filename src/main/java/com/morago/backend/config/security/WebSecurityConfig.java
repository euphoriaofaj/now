package com.morago.backend.config.security;

import com.morago.backend.config.security.handler.RestAccessDeniedHandler;
import com.morago.backend.config.security.handler.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailService customUserDetailService;

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint; // 401
    private final RestAccessDeniedHandler restAccessDeniedHandler;// 403

    private final Environment env;

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger/**"
    };

    private static final String[] WS_WHITELIST = {
            "/ws/**"
    };

    private static final String[] CORS_WHITELIST = {
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "http://localhost:5174",
            "http://127.0.0.1:5174",
            "http://localhost:5175",
            "http://127.0.0.1:5175"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean isLocalLike = Arrays.stream(env.getActiveProfiles())
                .anyMatch(p -> p.equalsIgnoreCase("local") || p.equalsIgnoreCase("dev"));
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // 401
                        .accessDeniedHandler(restAccessDeniedHandler)           // 403
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/password/reset/request").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/password/reset/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/password/reset/confirm").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/translators/*/rating").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/translators/*/rating").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/translators/**").hasAnyRole("TRANSLATOR", "ADMIN", "USER")


                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(WS_WHITELIST).permitAll()
                        .requestMatchers("/ws-health/**", "/ws-native/**", "/send-test", "/api/test/**").permitAll()
                        .requestMatchers("/api/me/**").hasAnyRole("USER","TRANSLATOR","ADMIN")
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/translator/**", "/translators/**").hasAnyRole("TRANSLATOR", "ADMIN")
                        .anyRequest().authenticated()
                )
                .cors(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration c = new CorsConfiguration();
        
        c.setAllowedOrigins(Arrays.asList(CORS_WHITELIST));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}
