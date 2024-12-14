package io.k8screen.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/** Provides JWT services */
@Component
public class JwtUtil {

  private final String accessKey =
      "561744090F30C5564F793305AB783BC96545E88EE42A7ED0CED578C72CE132A3";

  private final String refreshKey =
      "42DCCF1E0B06EF601CF9DCDA0ED3877F4F0DB32FAE243DC5F0C681DB09AFB454";

  public @NotNull Object getClaim(
      @NotNull final String signKey, final @NotNull String claimKey, final @NotNull String token) {

    return this.extractClaim(token, signKey, claims -> claims.get(claimKey, String.class));
  }

  public @NotNull String getAccessClaim(final @NotNull String token) {
    return this.extractClaim(token, this.accessKey, claims -> claims.get("username", String.class));
  }

  public @NotNull String getRefreshClaim(final @NotNull String token) {

    return this.extractClaim(
        token, this.refreshKey, claims -> claims.get("username", String.class));
  }

  public @NotNull <T> T extractClaim(
      final @NotNull String token,
      final @NotNull String signKey,
      final @NotNull Function<Claims, T> claimsResolver) {
    final Claims claims = this.extractAllClaims(token, signKey);
    return claimsResolver.apply(claims);
  }

  public @NotNull Claims extractAllClaims(
      final @NotNull String token, final @NotNull String signKey) {
    return Jwts.parser()
        .verifyWith(this.getSignKey(signKey))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public @NotNull String generateToken(
      @NotNull final String signKey, final @NotNull Map<String, Object> claims, final int expMin) {

    final Instant now = Instant.now();
    final Instant futureInstant = now.plusSeconds((long) expMin * 60);
    final ZonedDateTime zonedDateTime = futureInstant.atZone(ZoneId.systemDefault());

    final Date expirationDate = Date.from(zonedDateTime.toInstant());

    return Jwts.builder()
      .header()
      .add(
        Map.of(
          "alg", "HS256",
          "typ", "JWT"))
      .and()
      .claims(claims)
      .issuedAt(new Date(System.currentTimeMillis()))
      .expiration(expirationDate)
      .signWith(this.getSignKey(signKey))
      .compact();
  }

  public @NotNull String generateAccessToken(final @NotNull String username) {
    return this.generateToken(this.accessKey, Map.of("username", username), 1);
  }

  public @NotNull String generateRefreshToken(final @NotNull String username) {
    return this.generateToken(this.refreshKey, Map.of("username", username), 60 * 24);
  }

  private @NotNull SecretKey getSignKey(final @NotNull String signKey) {
    final byte[] keyBytes = Decoders.BASE64.decode(signKey);
    return new SecretKeySpec(keyBytes, "HmacSHA256");
  }
}
