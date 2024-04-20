package com.example.cinema_back_end.services;

import com.example.cinema_back_end.dtos.BookingRequestDTO;
import com.example.cinema_back_end.entities.FeedBack;

public interface IFeedBackService {
    public FeedBack createFeedbackForMovie(int movieId, String content, Double rate, Integer createdBy);
    public FeedBack provideFeedbackOnFeedback(int parentFeedbackId, String content, Double rate, Integer createdBy);
    public FeedBack updateFeedback(int feedbackId, String newContent, Double newRate, Integer updatedBy);
    public void deleteFeedback(int feedbackId, Integer deletedBy);


}
