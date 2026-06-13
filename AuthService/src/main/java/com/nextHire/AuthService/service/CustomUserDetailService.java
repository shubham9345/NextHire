package com.nextHire.AuthService.service;

import com.nextHire.AuthService.entity.UserInfo;
import com.nextHire.AuthService.entity.UserInfoDetails;
import com.nextHire.AuthService.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userDetail = userInfoRepository.findByUsername(username);
        if (userDetail == null) {
            throw new UsernameNotFoundException("user is not find with this " + username);
        }
        return new UserInfoDetails(userDetail);
    }
}