package com.tms.transportationManagementSystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil
{
    private static final String SECRET= "mySuperSecretKeyForJwtAuthentication123456789";

    private final Key key =  Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateTokenFromMail(String email)
    {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365))
                .signWith(key)
                .compact();
    }

    public Claims validateAndExtractClaimsFromTOken(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmailFromToken(String token)
    {
        return validateAndExtractClaimsFromTOken(token).getSubject();
    }

    public Date getTokenExpirationDate(String token)
    {
        return validateAndExtractClaimsFromTOken(token).getExpiration();
    }

    public boolean isTokenExpired(String token)
    {
        return getTokenExpirationDate(token).before(new Date());
    }
}