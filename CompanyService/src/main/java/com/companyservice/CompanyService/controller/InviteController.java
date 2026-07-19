package com.companyservice.CompanyService.controller;

import com.companyservice.CompanyService.dto.InviteRequest;
import com.companyservice.CompanyService.dto.JobResponseDto;
import com.companyservice.CompanyService.serviceImpl.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @PostMapping("/invite")
    public ResponseEntity<String> inviteCandidate(
            @RequestBody InviteRequest request) {

        return ResponseEntity.ok(
                inviteService.inviteCandidate(request)
        );
    }
    @GetMapping("/all-invites/{userId}")
    public ResponseEntity<List<JobResponseDto>> inviteJob(
            @PathVariable UUID userId) {

        List<JobResponseDto> jobs =
                inviteService.findInvitesByUserId(userId);

        return ResponseEntity.ok(jobs);
    }
}