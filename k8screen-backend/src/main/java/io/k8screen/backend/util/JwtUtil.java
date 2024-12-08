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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/** Provides JWT services */
@Component
public class JwtUtil {

  public static final String SIGN_KEY =
      "561744090F30C5564F793305AB783BC96545E88EE42A7ED0CED578C72CE132A3";

  /**
   * Gets the claim from the token.
   *
   * @param signKey sign key of the token
   * @param claimKey name of the claim
   * @param token JWT token
   * @return Object
   */
  public @NotNull Object getClaim(
      @NotNull final String signKey, final @NotNull String claimKey, final @NotNull String token) {

    return this.extractClaim(token, signKey, claims -> claims.get(claimKey, String.class));
  }

  /**
   * Extracts the claim on the token.
   *
   * @param token JWT token
   * @param signKey sign key of the token
   * @param claimsResolver lambda function which resolves the claims on the token
   * @return <T> T
   */
  public @NotNull <T> T extractClaim(
      final @NotNull String token,
      final @NotNull String signKey,
      final @NotNull Function<Claims, T> claimsResolver) {
    final Claims claims = this.extractAllClaims(token, signKey);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts the all claims on the token.
   *
   * @param token JWT token
   * @param signKey sign key of the token
   * @return Claims
   */
  public @NotNull Claims extractAllClaims(
      final @NotNull String token, final @NotNull String signKey) {
    return Jwts.parser()
        .verifyWith(this.getSignKey(signKey))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Generates a token.
   *
   * @param signKey sign key of the token
   * @param claims claims of the token
   * @param expDay expiration day of the token
   * @return Map<String, String>
   */
  public @NotNull Map<String, String> generateToken(
      @NotNull final String signKey, final @NotNull Map<String, Object> claims, final int expDay) {

    final String token = this.createToken(claims, signKey, expDay);
    return Map.of("token", token);
  }

  /**
   * Creates the token.
   *
   * @param claims claims of the token
   * @param signKey sign key of the token
   * @param expMin expiration day of the token
   * @return String
   */
  private @NotNull String createToken(
      final @NotNull Map<String, Object> claims, final @NotNull String signKey, final int expMin) {

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

  /**
   * Gets the sign key as a SecretKeySpec object.
   *
   * @param signKey sign key of the token
   * @return SecretKey
   */
  private @NotNull SecretKey getSignKey(final @NotNull String signKey) {
    final byte[] keyBytes = Decoders.BASE64.decode(signKey);
    return new SecretKeySpec(keyBytes, "HmacSHA256");
  }

  public Boolean validateToken(String token, UserDetails userDetails, boolean isAccessToken) {
    final String username = (String) getClaim(SIGN_KEY, userDetails.getUsername(), token);
    return (username.equals(userDetails.getUsername()));
  }

}
