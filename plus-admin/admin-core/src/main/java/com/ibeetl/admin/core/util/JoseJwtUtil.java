package com.ibeetl.admin.core.util;

import cn.hutool.core.map.MapUtil;
import java.util.Map;
import java.util.Random;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于jose4j的jwt库工具类。 包括：生成，验证，解析负载。<br/>
 * 可以用对称加密算法在此对token进行加密，逻辑上可以每隔一周或者一天，动态生成对称加密算法的密钥，然后防止破解。<br/>
 * 这样可以用jwt来承担更多的数据传递。至于客官怎么选择——萝卜青菜各有所爱<br/>
 *
 * @author 一日看尽长安花
 */
public class JoseJwtUtil {
  private static Logger logger = LoggerFactory.getLogger(JoseJwtUtil.class);

  public static String generateJwtJson(String uid) {
    JwtClaims jwtClaims = new JwtClaims();
    jwtClaims.setExpirationTimeMinutesInTheFuture(30);
    /* 以分钟为单位的过期时间 */
    /* who creates the token and signs it */
    jwtClaims.setIssuer("Issuer");
    /* to whom the token is intended to be sent */
    jwtClaims.setAudience("Audience");
    /* a unique identifier for the token */
    jwtClaims.setGeneratedJwtId();
    /* when the token was issued/created (now) */
    jwtClaims.setIssuedAtToNow();
    /* time before which the token is not yet valid (2 minutes ago) */
    jwtClaims.setNotBeforeMinutesInThePast(2);
    /*主题：签证*/
    jwtClaims.setSubject("Bearer");
    /*用户id*/
    jwtClaims.setClaim("uid", uid);
    /*登录时间*/
    jwtClaims.setClaim("ltm", System.currentTimeMillis());

    RsaJsonWebKey rsaJsonWebKey = RsaJsonWebKeyBuilder.getRasJsonWebKeyInstance();
    JsonWebSignature jsonWebSignature = new JsonWebSignature();
    jsonWebSignature.setPayload(jwtClaims.toJson());
    jsonWebSignature.setKey(rsaJsonWebKey.getPrivateKey());
    jsonWebSignature.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
    jsonWebSignature.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    String jwt = "invalid jwt";
    try {
      jwt = jsonWebSignature.getCompactSerialization();
    } catch (JoseException e) {
      logger.error(
          "can't generate jwt of user: {}. detail see next follow: \n {} ",
          uid,
          e.getLocalizedMessage());
    }
    return jwt;
  }

  public static boolean verifyJwtJson(String token) {
    return JoseJwtUtil.parsePayload(token).isEmpty();
  }

  public static Map<String, Object> parsePayload(String token) {
    JwtConsumer jwtConsumer =
        new JwtConsumerBuilder()
            .setRequireExpirationTime() // the JWT must have an expiration time
            .setMaxFutureValidityInMinutes(30) // but the  expiration time can't be too crazy
            .setAllowedClockSkewInSeconds(30) // 允许校准过期时间的偏差30秒
            .setRequireSubject() // the JWT must have a subject claim
            .setExpectedIssuer("Issuer") // whom the JWT needs to have been issued by
            .setExpectedAudience("Audience") // to whom the JWT is intended for
            .setVerificationKey(
                RsaJsonWebKeyBuilder.getRasJsonWebKeyInstance()
                    .getPublicKey()) // verify the signature with the public key
            .build(); // create the JwtConsumer instance
    Map<String, Object> claimsMap = MapUtil.newHashMap(0);
    try {
      JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
      claimsMap = jwtClaims.getClaimsMap();
    } catch (InvalidJwtException e) {
      logger.error("parser token fail.detail see next follow: {} ", e.getLocalizedMessage());
    }
    return claimsMap;
  }

  private static class RsaJsonWebKeyBuilder {
    private static volatile RsaJsonWebKey rsaJsonWebKey;

    private RsaJsonWebKeyBuilder() {}

    public static RsaJsonWebKey getRasJsonWebKeyInstance() {
      if (rsaJsonWebKey == null) {
        synchronized (RsaJsonWebKey.class) {
          if (rsaJsonWebKey == null) {
            try {
              rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
              rsaJsonWebKey.setKeyId(String.valueOf(new Random().nextLong()));
            } catch (Exception e) {
              return null;
            }
          }
        }
      }
      return rsaJsonWebKey;
    }
  }
}
