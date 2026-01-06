package ee.reimosi.lotto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var user  = User.withUsername("user").password(encoder.encode("user123")).roles("USER").build();
        var admin = User.withUsername("admin").password(encoder.encode("admin123")).roles("ADMIN","USER").build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Public: Swagger + Actuator
                        .requestMatchers("/",
                                "/error",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/actuator/health",
                                "/actuator/info"
                                ).permitAll()

                        // Read (USER/ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/draws/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/analysis/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/generate/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/generate/**").hasAnyRole("USER","ADMIN")

                        // Write (ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/draws/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/draws/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/draws/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").hasRole("ADMIN")

                        // Everything else requires auth
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
