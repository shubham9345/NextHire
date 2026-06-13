package com.nextHire.AuthService.dto;

import lombok.Getter;
import lombok.Setter;

import static com.nextHire.AuthService.utils.ConstantUtil.TOKEN_EXPIRATION_TIME;

@Getter
@Setter
public class JwtResponse {
    private final String jwtToken;
    private final String  expirationTimes;
    private String roles;

    public JwtResponse(String jwtToken,String roles){
        this.jwtToken = jwtToken;
        this.expirationTimes = ((TOKEN_EXPIRATION_TIME) / (1000 * 60)) + " minutes";
        this.roles = roles;

    }
}