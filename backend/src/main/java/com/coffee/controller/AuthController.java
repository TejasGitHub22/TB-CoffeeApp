package com.coffee.controller;

import com.coffee.dto.LoginRequest;
import com.coffee.dto.LoginResponse;
import com.coffee.model.Role;
import com.coffee.model.User;
import com.coffee.repository.UserRepository;
import com.coffee.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public AuthController(UserRepository userRepository, JwtService jwtService) {
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		return userRepository.findByUsername(request.getUsername())
				.filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
				.map(u -> ResponseEntity.ok(new LoginResponse(generate(u))))
				.orElse(ResponseEntity.status(401).build());
	}

	private String generate(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.getRole().name());
		return jwtService.generateToken(user.getUsername(), claims);
	}
}