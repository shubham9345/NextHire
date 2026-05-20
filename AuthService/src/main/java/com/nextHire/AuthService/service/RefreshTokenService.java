package com.nextHire.AuthService.service;

import com.nextHire.AuthService.entity.RefreshToken;
import com.nextHire.AuthService.entity.User;
import com.nextHire.AuthService.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenDuration;

    public RefreshToken createRefreshToken(
            User user,
            String token
    ) {

        repository.findByUser_Id(user.getId())
                .ifPresent(repository::delete);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .user(user)
                        .token(token)
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusSeconds(
                                                refreshTokenDuration / 1000
                                        )
                        )
                        .revoked(false)
                        .build();

        return repository.save(refreshToken);
    }
    public void revokeToken(String token) {

        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"
                                ));

        refreshToken.setRevoked(true);

        repository.save(refreshToken);

        log.info("Refresh token revoked");
    }
    public RefreshToken verifyRefreshToken(
            String token
    ) {

        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid refresh token"
                                ));

        if (refreshToken.isRevoked()) {

            throw new RuntimeException(
                    "Refresh token revoked"
            );
        }

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        return refreshToken;
    }
}
