package com.org.InterviewExperience.controller;

import com.org.InterviewExperience.model.Interview;
import com.org.InterviewExperience.service.InterviewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {
    @Autowired
    private InterviewServiceImpl interviewService;

    @PostMapping("/share/{userId}")
    public String shareInterview(@RequestBody Interview interview, @PathVariable int userId) {
        return interviewService.ShareInterviewExperience(interview, userId);
    }

    @GetMapping("/all-interview")
    public ResponseEntity<List<Interview>> allInterview() {
        List<Interview> interviewList = interviewService.AllInterview();
        return new ResponseEntity<>(interviewList, HttpStatus.OK);
    }

    @GetMapping("/all-interview/{userId}")
    public ResponseEntity<List<Interview>> allInterviewByUserId(@PathVariable Long userId) {
        List<Interview> interviewList = interviewService.AllInterviewByUserId(userId);
        return new ResponseEntity<>(interviewList, HttpStatus.OK);
    }
    @DeleteMapping("/delete/{interviewId}")
    public String deleteQuiz(@PathVariable Long  interviewId){
        return interviewService.deleteInterview(interviewId);
    }
}
