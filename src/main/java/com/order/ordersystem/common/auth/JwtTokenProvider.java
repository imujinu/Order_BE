package com.order.ordersystem.common.auth;

import com.order.ordersystem.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secretKeyAt}")
    private String secretKey;

    @Value("${jwt.expirationAt}")
    private int expirationAt;

    private Key secret_at_key;

    @PostConstruct
    public void makeKey(){
        secret_at_key = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName() );
    }
    public String createToken(Member member){
        String email = member.getEmail();
        String role = String.valueOf(member.getRole());

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role",role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expirationAt*60*1000L))
                .signWith(secret_at_key)
                .compact();
        return token;

    }

    public String createRtToken(Member member) {
        // 유효기간이 긴  rt 토큰 생성

        // rt 토큰 reids에 저장
        return null;
    }
}
