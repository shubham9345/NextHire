package com.companyservice.CompanyService.serviceImpl;

import com.companyservice.CompanyService.dto.InviteRequest;
import com.companyservice.CompanyService.dto.JobResponseDto;
import com.companyservice.CompanyService.dto.UserJobApplicationResponseDto;
import com.companyservice.CompanyService.entity.Invite;
import com.companyservice.CompanyService.repository.InviteRepository;
import com.companyservice.CompanyService.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final JobService jobService;

    public String inviteCandidate(InviteRequest request) {

        if (inviteRepository.existsByUserIdAndJobId(
                request.getUserId(),
                request.getJobId())) {

            return "Candidate already invited for this job.";
        }

        Invite invite = Invite.builder()
                .userId(request.getUserId())
                .jobId(request.getJobId())
                .build();

        inviteRepository.save(invite);

        return "Invitation sent successfully.";
    }
    public List<JobResponseDto> findInvitesByUserId(UUID userId) {

        return inviteRepository.findByUserId(userId)
                .stream()
                .map(invite -> jobService.getJob(invite.getJobId()))
                .toList();
    }
}