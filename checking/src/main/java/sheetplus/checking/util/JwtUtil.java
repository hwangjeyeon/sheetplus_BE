package sheetplus.checking.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sheetplus.checking.domain.dto.MemberInfoDto;


import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpire;
    private final long refreshTokenExpire;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access.expiration_time}") long accessTokenExpireTime,
            @Value("${jwt.refresh.expiration_time}") long refreshTokenExpireTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpire = accessTokenExpireTime;
        this.refreshTokenExpire = refreshTokenExpireTime;
    }

    public String createAccessToken(MemberInfoDto memberInfoDto){
        return createToken(memberInfoDto, accessTokenExpire);
    }

    public String createRefreshToken(MemberInfoDto memberInfoDto){
        return createToken(memberInfoDto, refreshTokenExpire);
    }

    private String createToken(MemberInfoDto memberInfoDto, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberInfoDto.getId());
        claims.put("email", memberInfoDto.getEmail());
        claims.put("memberType", memberInfoDto.getMemberType());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getMemberId(String token){
        return parseClaims(token).get("memberId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims parseClaims(String accessToken){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
