package com.inn.weatherApp.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.module.ModuleDescriptor;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
@Service
public class JWTUtil {
    // private JWTUtil(){};
    //private String secret = "mujcrazyprojektnastin";
    private final String secretString = "mujcrazyprojektnastinaleikdybytohlenebylodostatecnedlouhetakjetodocelacrazynenechaputoasilmao";
    private final byte[] secretBytes = secretString.getBytes();
    private final Key secretKey = Keys.hmacShaKeyFor(secretBytes);

    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }
    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }
    public <T> T extractClaims(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String token){
        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder();
        jwtParserBuilder.setSigningKey(secretKey);
        JwtParser parser = jwtParserBuilder.build();
        return parser.parseClaimsJws(token).getBody();
    }

    private boolean checkExpiredToken(String token){
        return extractExpiration(token).before(new Date());
    }
    public String generateToken(String username, String role){
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",role);
        return createToken(claims,username);

    }
    private String createToken(Map<String,Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000L * 60 *60 *60*10))
                .signWith(secretKey,SignatureAlgorithm.HS256).compact();

    }
    public boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        if (Objects.isNull(username)){
            return false;
        }
        return (username.equals(userDetails.getUsername()) && !checkExpiredToken(token));
    }

}