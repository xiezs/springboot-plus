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
 * 基于jose4j的jwt库工具类。 包括：生成，验证，解析负载。<br>
 * 可以用对称加密算法在此对token进行加密，逻辑上可以每隔一周或者一天，动态生成对称加密算法的密钥，然后防止破解。<br>
 * 这样可以用jwt来承担更多的数据传递。至于客官怎么选择——萝卜青菜各有所爱<br>
 * iss: jwt签发者 sub: jwt所面向的用户 aud: 接收jwt的一方 exp: jwt的过期时间，这个过期时间必须要大于签发时间 nbf: 定义在什么时间之前，该jwt都是不可用的.
 * iat: jwt的签发时间 jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
 *
 * @author 一日看尽长安花
 */
public class JoseJwtUtil {
  private static Logger logger = LoggerFactory.getLogger(JoseJwtUtil.class);
  private static int EXPIRATION_TIME = 10;
  /**
   * Method generateJwtToken ... <br>
   * 生成jwt token字符串
   *
   * @param uid of type String
   * @return String
   */
  public static String generateJwtToken(String uid) {
    JwtClaims jwtClaims = JoseJwtUtil.getJwtClaims(uid);
    JsonWebSignature jsonWebSignature = JoseJwtUtil.getJsonWebSignature(jwtClaims);
    String jwt = "invalid jwt";
    try {
      jwt = jsonWebSignature.getCompactSerialization();
    } catch (JoseException e) {
      logger.error(
          "can't generate jwt of {} user.: detail see next follow: \n {} ",
          uid,
          e.getLocalizedMessage());
    }
    return jwt;
  }

  /**
   * Method verifyJwtJson ... <br>
   * 验证jwt是否过期<br>
   *
   * @param token of type String
   * @return boolean
   */
  public static boolean verifyJwtJson(String token) {
    return JoseJwtUtil.parsePayload(token).isEmpty();
  }

  /**
   * Method refreshIssuedAtTime ...<br>
   * 刷新颁发时间，达到重置30分钟时间验证窗口
   *
   * @param token of type String
   * @return String
   */
  public static String refreshIssuedAtTime(String token) {
    JwtConsumer jwtConsumer = JoseJwtUtil.getJwtConsumer(token);
    JwtClaims jwtClaims = null;
    String jwt = "invalid jwt";
    try {
      jwtClaims = jwtConsumer.processToClaims(token);
      jwtClaims.setIssuedAtToNow();
      jwtClaims.setExpirationTimeMinutesInTheFuture(EXPIRATION_TIME);
      JsonWebSignature jsonWebSignature = JoseJwtUtil.getJsonWebSignature(jwtClaims);
      jwt = jsonWebSignature.getCompactSerialization();
    } catch (InvalidJwtException e) {
      logger.error("parser token fail.detail see next follow: {} ", e.getLocalizedMessage());
    } catch (JoseException e) {
      logger.error("can't refresh jwt : detail see next follow: \n {} ", e.getLocalizedMessage());
    }
    return jwt;
  }

  /**
   * Method parsePayload ... 解析请求中的jwt token，返回map是为了避免业务中使用jose的类。 若有必要可以自定义一个类
   *
   * @param token of type String
   * @return Map<String, Object>
   */
  public static Map<String, Object> parsePayload(String token) {
    JwtConsumer jwtConsumer = JoseJwtUtil.getJwtConsumer(token);
    Map<String, Object> claimsMap = MapUtil.newHashMap(0);
    try {
      JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
      claimsMap = jwtClaims.getClaimsMap();
    } catch (InvalidJwtException e) {
      logger.error("parser token fail.detail see next follow: {} ", e.getLocalizedMessage());
    }
    return claimsMap;
  }

  public static JsonWebSignature getJsonWebSignature(JwtClaims jwtClaims) {
    RsaJsonWebKey rsaJsonWebKey = RsaJsonWebKeyBuilder.getRasJsonWebKeyInstance();
    JsonWebSignature jsonWebSignature = new JsonWebSignature();
    jsonWebSignature.setPayload(jwtClaims.toJson());
    jsonWebSignature.setKey(rsaJsonWebKey.getPrivateKey());
    jsonWebSignature.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
    jsonWebSignature.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    return jsonWebSignature;
  }

  /**
   * Method getJwtClaims ... <br>
   * 获取jwt 签名类 <br>
   * 包含了登录时间，所以尽可能只用于登录的jwt<br>
   *
   * @param uid of type String
   * @return JwtClaims
   */
  public static JwtClaims getJwtClaims(String uid) {
    JwtClaims jwtClaims = new JwtClaims();
    /*设置过期时间*/
    /* 颁发签名者 */
    jwtClaims.setIssuer("Issuer");
    /* 接收签名者 */
    jwtClaims.setAudience("Audience");
    /* a unique identifier for the token */
    jwtClaims.setGeneratedJwtId();
    /* when the token was issued/created (now) */
    jwtClaims.setIssuedAtToNow();
    /* 以分钟为单位的过期时间 */
    jwtClaims.setExpirationTimeMinutesInTheFuture(EXPIRATION_TIME);
    /* 参照jwt规范中nbf字段。用于解决多服务中时间差问题 */
    jwtClaims.setNotBeforeMinutesInThePast(2);
    /*主题：签证*/
    jwtClaims.setSubject("Bearer");
    /*用户id*/
    jwtClaims.setClaim("uid", uid);
    /*登录时间*/
    jwtClaims.setClaim("ltm", System.currentTimeMillis());

    return jwtClaims;
  }

  /**
   * Method getJwtConsumer ... 获取 jwt 验证消费者
   *
   * @param token of type String
   * @return JwtConsumer
   */
  public static JwtConsumer getJwtConsumer(String token) {
    JwtConsumer jwtConsumer =
        new JwtConsumerBuilder()
            .setRequireExpirationTime() // the JWT must have an expiration time
            .setMaxFutureValidityInMinutes(
                EXPIRATION_TIME) // but the  expiration time can't be too crazy
            .setAllowedClockSkewInSeconds(30) // 允许校准过期时间的偏差30秒
            .setRequireSubject() // the JWT must have a subject claim
            .setExpectedIssuer("Issuer") // whom the JWT needs to have been issued by
            .setExpectedAudience("Audience") // to whom the JWT is intended for
            .setVerificationKey(
                RsaJsonWebKeyBuilder.getRasJsonWebKeyInstance()
                    .getPublicKey()) // verify the signature with the public key
            .build(); // create the JwtConsumer instance
    return jwtConsumer;
  }

  /**
   * Class RsaJsonWebKeyBuilder : <br>
   * 描述：线程安全
   *
   * @author 一日看尽长安花 Created on 2020/1/11
   */
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
