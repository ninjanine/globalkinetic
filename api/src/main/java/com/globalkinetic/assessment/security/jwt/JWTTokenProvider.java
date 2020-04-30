package com.globalkinetic.assessment.security.jwt;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTTokenProvider implements InitializingBean {

    private static final String ROLES_KEY = "auth";

    private Key key;

    @Value("${jwt.tokenValidityInSeconds}")
    private long tokenValidityInSeconds;

    @Value("${jwt.secret}")
    private String base64Secret;

    public JWTTokenProvider() { }


    @Override
    public void afterPropertiesSet() {
        final byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(final Authentication authentication) {
        final String roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        final long now = (new Date()).getTime();
        final Date validity = new Date(now + (this.tokenValidityInSeconds * 100));

        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(ROLES_KEY, roles)
            .signWith( SignatureAlgorithm.HS512, key)
            .setExpiration(validity)
            .compact();
    }

    public Authentication getAuthentication(final String token) {
        final Claims claims = Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(token)
            .getBody();

        final Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(ROLES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        final User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ignored) {}
        return false;
    }
}
