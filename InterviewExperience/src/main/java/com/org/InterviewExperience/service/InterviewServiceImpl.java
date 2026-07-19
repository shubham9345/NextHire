package com.org.InterviewExperience.service;

import com.org.InterviewExperience.model.CodingQuestion;
import com.org.InterviewExperience.model.Interview;
import com.org.InterviewExperience.model.InterviewRound;
import com.org.InterviewExperience.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InterviewServiceImpl implements InterviewService {
    @Autowired
    private InterviewRepository interviewRepository;

    @Override
    public String ShareInterviewExperience(Interview interview, UUID userId) {
        if (interview == null) {
            throw new RuntimeException("interview experience body is null");
        }

        interview.setDate_post(LocalDate.now());
        interview.setUserId(userId);

        if (interview.getInterviewRoundList() != null) {
            for (InterviewRound round : interview.getInterviewRoundList()) {
                round.setInterview(interview); // set parent reference
            }
        } else {
            throw new RuntimeException("interview round list is null");
        }

        if (interview.getInterviewRoundList() != null) {
            for (InterviewRound round : interview.getInterviewRoundList()) {
                if (round.getCodingQuestionList() != null) {
                    for (CodingQuestion q : round.getCodingQuestionList()) {
                        q.setInterview_round(round);
                    }
                } else {
                    throw new RuntimeException("coding question is null");
                }
            }
        } else {
            throw new RuntimeException("interview round list is null");
        }

        interviewRepository.save(interview);
        return "Interview experience is added successfully";
    }

    @Override
    public List<Interview> AllInterview() {
        return interviewRepository.findAll();
    }

    @Override
    public List<Interview> AllInterviewByUserId(UUID userId) {
        return interviewRepository.findInterviewByUserId(userId);
    }
    @Override
    public String deleteInterview(Long interviewId) {
        if(!interviewRepository.existsById(interviewId)){
            throw new RuntimeException("interview is not found with interviewId " + interviewId);
        }
        interviewRepository.deleteById(interviewId);
        return "interview is deleted successfully with interviewId --> " + interviewId;
    }
}
