package com.nextHire.AuthService.controller;

import com.nextHire.AuthService.dto.*;
import com.nextHire.AuthService.entity.ErrorResponse;
import com.nextHire.AuthService.entity.UserInfo;
import com.nextHire.AuthService.enums.Role;
import com.nextHire.AuthService.repository.UserInfoRepository;
import com.nextHire.AuthService.security.JwtUtil;
import com.nextHire.AuthService.service.AuthService;
import com.nextHire.AuthService.serviceImpl.AuthServiceImpl;
import com.nextHire.AuthService.serviceImpl.CustomUserDetailService;
import com.nextHire.AuthService.serviceImpl.GoogleAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.nextHire.AuthService.utils.ConstantUtil.INVALID_CREDENTIAL;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService userInfoService;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private GoogleAuthService googleAuthService;

    @PostMapping("/signup")
    public ResponseEntity<UserInfo> Signup( @Valid @RequestBody SignupRequest signupRequest) {
        try {
            if (signupRequest.getPassword() == null || signupRequest.getUsername() == null) {
                throw new RuntimeException(INVALID_CREDENTIAL);

            }
            if (signupRequest.getPassword().equals(" ") || signupRequest.getUsername().equals(" ") || signupRequest.getPassword().isEmpty() || signupRequest.getUsername().isEmpty()) {
                throw new RuntimeException(INVALID_CREDENTIAL);
            }
            if (signupRequest.getRoles() == null) {
                signupRequest.setRoles(Role.CANDIDATE);
            }
            UserInfo newUser = userInfoService.AddUser(signupRequest);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/error")
    public ResponseEntity<ErrorResponse> error(HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Invalid username and password or your token is expired");
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(INVALID_CREDENTIAL);
        }

        UserInfo userDetails = userInfoRepository.findByUsername(request.getUsername());
        String token = this.jwtUtil.generateToken(userDetails.getUsername(), userDetails.getId());
        JwtResponse response = new JwtResponse(token,userDetails.getRoles().toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/company-login")
    public ResponseEntity<JwtResponse> BusinessLogin(@RequestBody JwtRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(INVALID_CREDENTIAL);
        }

        UserInfo userDetails = userInfoRepository.findByUsername(request.getUsername());
          if(userDetails.getRoles() == Role.CANDIDATE){
              throw new BadCredentialsException("Invalid credential");
          }
        String token = this.jwtUtil.generateToken(userDetails.getUsername(), userDetails.getId());
        JwtResponse response = new JwtResponse(token,userDetails.getRoles().toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ─── GOOGLE LOGIN / SIGNUP (CANDIDATE) ───────────────────────────────────────
    @PostMapping("/google")
    public ResponseEntity<JwtResponse> googleLogin(@RequestBody GoogleTokenRequest request) {
        try {
            UserInfo user = googleAuthService.verifyAndGetUser(request.getIdToken());
            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            JwtResponse response = new JwtResponse(token, user.getRoles().toString());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // ─── GOOGLE LOGIN / SIGNUP (COMPANY) ─────────────────────────────────────────
    @PostMapping("/company-google")
    public ResponseEntity<JwtResponse> companyGoogleLogin(@RequestBody GoogleTokenRequest request) {
        try {
            UserInfo user = googleAuthService.verifyAndGetUser(request.getIdToken());

            // Block candidates from using company login
            if (user.getRoles() == Role.CANDIDATE) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getId());
            JwtResponse response = new JwtResponse(token, user.getRoles().toString());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/all-user")
    public ResponseEntity<List<UserInfo>> allUser() {
        List<UserInfo> allUser = userInfoService.getAllUser();
        if (allUser == null) {
            throw new RuntimeException("no user is found");
        }
        return new ResponseEntity<>(allUser, HttpStatus.OK);
    }
}

