package com.coffee.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
	@Value("${jwt.secret:ZmFrZV9jb2ZmZWVfYmFja2VuZF9zZWNyZXRfMTIzNDU2}")
	private String secret;

	@Value("${jwt.expirationMs:86400000}")
	private long expirationMs;

	public String generateToken(String subject, Map<String, Object> claims) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);
		return Jwts.builder()
				.setSubject(subject)
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(exp)
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public Claims parseToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
	}
}