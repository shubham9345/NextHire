package com.org.InterviewExperience.service;

import com.org.InterviewExperience.model.Interview;

import java.util.List;

public interface InterviewService {
    public String ShareInterviewExperience(Interview interview, int userId);

    public List<Interview> AllInterview();

    public List<Interview> AllInterviewByUserId(Long userId);

    public String deleteInterview(Long interviewId);
}
