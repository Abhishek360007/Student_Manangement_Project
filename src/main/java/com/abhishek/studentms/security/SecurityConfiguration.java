package com.abhishek.studentms.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

	private static final String[] PUBLIC_PATHS = { "/", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
			"/h2-console/**", "/actuator/health/**" };

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_PATHS).permitAll().anyRequest().authenticated());
		http.httpBasic(withDefaults());
		// Stateless REST API authenticated with HTTP Basic, so CSRF tokens are not needed
		http.csrf(AbstractHttpConfigurer::disable);
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		// H2 console is rendered inside frames
		http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
		return http.build();
	}

}
