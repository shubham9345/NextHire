package com.nextHire.AuthService.dto;

public class GoogleTokenRequest {
    private String idToken;  // The token Google gives to the frontend
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}