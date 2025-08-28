package com.coffee.dto;

public class LoginResponse {
	private String accessToken;
	private String tokenType = "Bearer";
	private Long id;
	private String username;
	private String name;
	private String role;

	public LoginResponse() {}

	public LoginResponse(String accessToken, Long id, String username, String name, String role) {
		this.accessToken = accessToken;
		this.id = id;
		this.username = username;
		this.name = name;
		this.role = role;
	}

	public String getAccessToken() { return accessToken; }
	public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
	public String getTokenType() { return tokenType; }
	public void setTokenType(String tokenType) { this.tokenType = tokenType; }
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
}