package org.hugo.backend.users.app.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Date;

public class TokenJwtConfig {
    //public final static String SECRET_KEY ="algun-token-con-alguna-palabra-secreta";
    public final static Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public final static String PREFIX_TOKEN ="Bearer ";
    public final static String HEADER_AUTHORIZATION ="Authorization";
    public static final long TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000; // Un día en milisegundos
    public static final int TOKEN_EXPIRATION_TIME_IN_SECONDS = (int) (TOKEN_EXPIRATION_TIME / 1000); // Un día en segundos

    public final static String NAME_COOKIE_JWT ="jwt";

/*
* scp -i ~/Downloads/backend_login.pem ./spring-boot-usersapp-0.0.1-SNAPSHOT.jar ec2-user@ec2-54-234-184-139.compute-1.amazonaws.com:~/.
java -jar spring-boot-usersapp-0.0.1-SNAPSHOT.jar
* screen -d -m java -jar spring-boot-usersapp-0.0.1-SNAPSHOT.jar

 * */

    public static String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (header != null && header.startsWith(PREFIX_TOKEN)) {
            return header.replace(PREFIX_TOKEN, "");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(NAME_COOKIE_JWT)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Cookie createTokenCookie(String token) {
        Cookie cookie = new Cookie(NAME_COOKIE_JWT, token);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(TOKEN_EXPIRATION_TIME_IN_SECONDS);
        cookie.setPath("/");
        return cookie;
    }

    public static String generateToken(String email,Long expirationTimeMilis){
        return  Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMilis))
                .signWith(TokenJwtConfig.SECRET_KEY)
                .compact();
    }

}
