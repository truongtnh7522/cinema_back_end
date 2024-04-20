package com.example.cinema_back_end.apis;

import com.example.cinema_back_end.dtos.SeatDTO;
import com.example.cinema_back_end.entities.FeedBack;
import com.example.cinema_back_end.security.repo.IUserRepository;
import com.example.cinema_back_end.services.IFeedBackService;
import com.example.cinema_back_end.services.ISeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/feedback")
public class FeedbackApi {

    @Autowired
    private IFeedBackService feedBackService;
    @Autowired
    private IUserRepository userRepository;
    @PostMapping("/create")
    public ResponseEntity<FeedBack> createFeedback(@RequestParam("movieId") int movieId,
                                                   @RequestParam("content") String content,
                                                   @RequestParam("rate") Double rate,
                                                   Principal principal) {
        Integer createdBy =userRepository.findByUsername(principal.getName()).get().getId();
        FeedBack feedback = feedBackService.createFeedbackForMovie(movieId, content, rate, createdBy);
        return ResponseEntity.ok().body(feedback);
    }

    @PostMapping("/reply")
    public ResponseEntity<FeedBack> replyToFeedback(@RequestParam("parentFeedbackId") int parentFeedbackId,
                                                    @RequestParam("content") String content,
                                                    @RequestParam("rate") Double rate,
                                                    Principal principal) {
        Integer createdBy =userRepository.findByUsername(principal.getName()).get().getId();
        FeedBack feedback = feedBackService.provideFeedbackOnFeedback(parentFeedbackId, content, rate, createdBy);
        return ResponseEntity.ok().body(feedback);
    }
    @PutMapping("/update")
    public ResponseEntity<FeedBack> updateFeedback(@RequestParam("feedbackId") int feedbackId,
                                                   @RequestParam("newContent") String newContent,
                                                   @RequestParam("newRate") Double newRate,
                                                   Principal principal) {
        Integer updatedBy = userRepository.findByUsername(principal.getName()).get().getId();
        FeedBack feedback = feedBackService.updateFeedback(feedbackId, newContent, newRate, updatedBy);
        return ResponseEntity.ok().body(feedback);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFeedback(@RequestParam("feedbackId") int feedbackId,
                                               Principal principal) {
        Integer deletedBy = userRepository.findByUsername(principal.getName()).get().getId();
        feedBackService.deleteFeedback(feedbackId, deletedBy);
        return ResponseEntity.noContent().build();
    }



}
