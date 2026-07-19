package com.org.InterviewExperience.service;

import com.org.InterviewExperience.model.Interview;

import java.util.List;
import java.util.UUID;

public interface InterviewService {
    public String ShareInterviewExperience(Interview interview, UUID userId);

    public List<Interview> AllInterview();

    public List<Interview> AllInterviewByUserId(UUID userId);

    public String deleteInterview(Long interviewId);
}
