package com.coffee.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtService jwtService;

	public SecurityConfig(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
					.requestMatchers("/actuator/**").permitAll()
					.requestMatchers("/api/admin/**").hasRole("ADMIN")
					.requestMatchers("/api/facility/**").hasAnyRole("FACILITY", "ADMIN")
					.anyRequest().authenticated())
			.addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Component
	static class JwtAuthFilter extends OncePerRequestFilter {
		private final JwtService jwtService;

		JwtAuthFilter(JwtService jwtService) {
			this.jwtService = jwtService;
		}

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
			String auth = request.getHeader("Authorization");
			if (auth != null && auth.startsWith("Bearer ")) {
				String token = auth.substring(7);
				try {
					Claims claims = jwtService.parseToken(token);
					String username = claims.getSubject();
					String role = String.valueOf(claims.get("role"));
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
							List.of(new SimpleGrantedAuthority("ROLE_" + role)));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} catch (Exception e) {
					SecurityContextHolder.clearContext();
				}
			}
			filterChain.doFilter(request, response);
		}
	}
}