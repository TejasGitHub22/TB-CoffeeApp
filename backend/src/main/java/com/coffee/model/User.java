package com.coffee.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column
	private java.time.Instant createdAt;

	@Column
	private java.time.Instant lastLogin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public java.time.Instant getCreatedAt() { return createdAt; }
	public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
	public java.time.Instant getLastLogin() { return lastLogin; }
	public void setLastLogin(java.time.Instant lastLogin) { this.lastLogin = lastLogin; }
}