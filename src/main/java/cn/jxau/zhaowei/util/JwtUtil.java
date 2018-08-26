package cn.jxau.zhaowei.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;


/**
 * @ClassName JwtUtil
 * @Description TODO
 * @Author zhaowei
 * @Date 2018/8/27 0:56
 * @Version 1.0
 */
public class JwtUtil {
    public static String createJWT(String id, String issuer, String subject, long ttlMillis){
        SignatureAlgorithm hs256 = SignatureAlgorithm.HS256;

        return null;
    }

    public static void main(String[] args) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject("Joe").signWith(key).compact();
        System.out.println(jws);

        boolean result = Jwts.parser().setSigningKey(key)
                .parseClaimsJws(jws).getBody().getSubject().equals("Joe");
        System.out.println(result);

    }
}
