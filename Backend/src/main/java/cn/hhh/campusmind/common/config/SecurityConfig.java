package cn.hhh.campusmind.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // 放行
                        .requestMatchers(
                                "/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/login"
                        ).permitAll()
                        // 其他接口还是需要认证
                        .anyRequest().authenticated()
                )
                // 关闭 CSRF
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
