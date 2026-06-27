package com.nextHire.AuthService.repository;

import com.nextHire.AuthService.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {

    UserInfo findByUsername(String Username);
    UserInfo findByEmail(String email);
    UserInfo findByGoogleId(String googleId);

}