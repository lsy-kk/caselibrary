package com.lsykk.caselibrary.utils;
import com.lsykk.caselibrary.vo.ErrorCode;
import io.jsonwebtoken.*;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {

    private static final String jwtToken = "kkysl_is_genius";

    public static String createToken(Long userId){
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",userId);
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, jwtToken) // 签发算法，秘钥为jwtToken
                .setClaims(claims) // body数据，要唯一，自行设置
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)); // 一天的有效时间
        return jwtBuilder.compact();
    }

    public static ErrorCode checkToken(String token){
        try {
            Jwt parse = Jwts.parser().setSigningKey(jwtToken).parse(token);
            return null;
        }
        catch (ExpiredJwtException e){
            return ErrorCode.TOKEN_EXPIRED;
        }
        catch (Exception e){
            return ErrorCode.TOKEN_ERROR;
        }
    }

    public static String checkAndGetUserId(String token){
        try {
            Jwt parse = Jwts.parser().setSigningKey(jwtToken).parse(token);
            Map<String, Object> stringObjectMap = (Map<String, Object>)parse.getBody();
            if (stringObjectMap == null){
                return null;
            }
            return String.valueOf(stringObjectMap.get("userId"));
        }
        catch (Exception e){
            return null;
        }
    }
}
