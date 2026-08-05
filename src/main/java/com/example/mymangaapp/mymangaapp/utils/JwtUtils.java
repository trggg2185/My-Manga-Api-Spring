package com.example.mymangaapp.mymangaapp.utils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import com.example.mymangaapp.mymangaapp.entity.Role;
import com.example.mymangaapp.mymangaapp.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.example.mymangaapp.mymangaapp.entity.User;
import com.example.mymangaapp.mymangaapp.exception.AppException;
import com.example.mymangaapp.mymangaapp.exception.ResponseCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// Class tiện ích, cung cấp các hàm làm việc nhanh với jwt
// VD: tạo accesstoken, verify token
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.signer-key}")
    String signerKey;

    @Value("${jwt.access-token-validity-in-seconds}")
    long accessTokenValidityInSeconds;

    @Value("${jwt.refreshable-duration-in-seconds}")
    long refreshableDurationInSeconds;

    final InvalidatedTokenRepository invalidatedTokenRepository;
    
    // tạo access token
    public String generateAccessToken(User user) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(accessTokenValidityInSeconds);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("trg2k5.com")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expirationTime))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Không thể tạo access token do JOSEException!", e);
            throw new RuntimeException(e);
        }
        
    }

    // Lấy các claims từ token
    public JWTClaimsSet getClaimsSet(String token) {
       try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return signedJWT.getJWTClaimsSet();
       } catch (ParseException e) {
            throw new AppException(ResponseCode.UNAUTHENTICATED);
       }
    }

    // Lấy username từ token
    public String extractUsername(String token) {
        return getClaimsSet(token).getSubject();
    }

    // Lấy jwt id từ token
    public String extractJwtId(String token) {
        return getClaimsSet(token).getJWTID();
    }

    // Lấy thời gian hết hạn từ token
    public Date extractExpirationTime(String token) {
        return getClaimsSet(token).getExpirationTime();
    }
 
    // xác thực token (đúng chữ ký, chưa hết hạn), 2 TH: verify hoặc verify refresh
    public boolean verifyToken(String token, boolean isRefresh) {
        try {
            JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
                log.error("Token đã bị vô hiệu hoá!");
                return false;
            }

            // Nếu là hàm refresh token thì thời gian hết hạn bằng tg issua token đó + refreshable duration
            // Nếu là hàm verify token bình thường thì cứ trả về expiration time
            Date exprirationTime = (isRefresh)
                    ? new Date(signedJWT
                            .getJWTClaimsSet()
                            .getIssueTime()
                            .toInstant()
                            .plus(refreshableDurationInSeconds, ChronoUnit.SECONDS)
                            .toEpochMilli())
                    : signedJWT.getJWTClaimsSet().getExpirationTime();

            return signedJWT.verify(verifier) && exprirationTime.after(new Date());

        } catch (JOSEException | ParseException e) {
            log.error("Xác thực token thất bại! Hoặc token không hợp lệ");
            return false;
        }
    }

    // overload giữa nguyên method cho jwt authentication filter
    public boolean verifyToken(String token) {
        return verifyToken(token, false);
    }

    public String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles())) {

            for (Role role : user.getRoles()) {
                // Tuy user và role là nhiều - nhiều
                // nhưng ta sẽ ko gán user có nhiều role
                stringJoiner.add(role.getName());

                // Nếu đã có role admin thì scope chỉ cần 1 role admin thôi, ko cần permission
                // vì với admin thì all permissions đều pass hết
                if (role.getName().equals("ADMIN")) {
                    return stringJoiner.toString();
                }

                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission ->
                            stringJoiner.add(permission.getName()));
                }
            }

        }

        return stringJoiner.toString();
    }

}
