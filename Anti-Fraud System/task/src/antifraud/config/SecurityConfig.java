package antifraud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .csrf((csrf) -> csrf.disable())                           // For modifying requests via Postman
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                )
                .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
                .authorizeHttpRequests(requests -> requests                     // manage access
                                .requestMatchers("/actuator/shutdown").permitAll()      // needs to run test
                                .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                                .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMINISTRATOR")

                                .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                                .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole("SUPPORT")

                                .requestMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasRole("SUPPORT")
                                .requestMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasRole("SUPPORT")
                                .requestMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/*").hasRole("SUPPORT")

                                .requestMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasRole("SUPPORT")
                                .requestMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasRole("SUPPORT")
                                .requestMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/*").hasRole("SUPPORT")

                                .requestMatchers(HttpMethod.GET, "/api/antifraud/history").hasRole("SUPPORT")
                                .requestMatchers(HttpMethod.GET, "/api/antifraud/history/*").hasRole("SUPPORT")

                        // other matchers
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                // other configurations
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
                .password("pass1")
                .roles()
                .build();
        UserDetails user2 = User.withUsername("user2")
                .password("pass2")
                .roles()
                .build();

        return new InMemoryUserDetailsManager(user1, user2);
    }
}
