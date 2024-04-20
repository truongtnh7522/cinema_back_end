package com.example.cinema_back_end.services;

import com.example.cinema_back_end.dtos.BookingRequestDTO;
import com.example.cinema_back_end.entities.*;
import com.example.cinema_back_end.repositories.*;
import com.example.cinema_back_end.security.repo.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FeedBackService implements IFeedBackService {
    @Autowired
    private IFeedBackRepository feedBackRepository;
    @Autowired
    private IMovieRepository movieRepository;

    @Override
    @Transactional
    public FeedBack createFeedbackForMovie(int movieId, String content, Double rate, Integer createdBy) {
        // Retrieve the movie by its ID
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            FeedBack feedback = new FeedBack();
            feedback.setContent(content);
            feedback.setRate(rate);
            feedback.setCreateBy(createdBy);
            feedback.setCreateDate(new Date());
            // Associate feedback with the movie
            feedback.setMovie(movie);
            return feedBackRepository.save(feedback);
        } else {
            throw new IllegalArgumentException("Movie not found with ID: " + movieId);
        }
    }

    @Override
    @Transactional
    public FeedBack provideFeedbackOnFeedback(int parentFeedbackId, String content, Double rate, Integer createdBy) {
        // Retrieve the parent feedback by its ID
        Optional<FeedBack> optionalParentFeedback = feedBackRepository.findById(parentFeedbackId);

        if (optionalParentFeedback.isPresent()) {
            FeedBack parentFeedback = optionalParentFeedback.get();
            // Kiểm tra xem parent feedback có một movie được liên kết không
            if (parentFeedback.getMovie() != null) {
                // Nếu có, tiếp tục với xử lý
                Movie movie = parentFeedback.getMovie();
                FeedBack feedback = new FeedBack();
                feedback.setContent(content);
                feedback.setRate(rate);
                feedback.setCreateBy(createdBy);
                feedback.setCreateDate(new Date());
                feedback.setMovie(movie);
                // Associate feedback with the parent feedback
                feedback.setParentFeedback(parentFeedback);
                return feedBackRepository.save(feedback);
            } else {
                // Nếu không có movie liên kết với parent feedback
                throw new IllegalArgumentException("Parent feedback does not have a movie.");
            }
        } else {
            // Nếu không tìm thấy parent feedback
            throw new IllegalArgumentException("Parent feedback not found with ID: " + parentFeedbackId);
        }
    }
    @Override
    @Transactional
    public FeedBack updateFeedback(int feedbackId, String newContent, Double newRate, Integer updatedBy) {
        // Tìm kiếm bình luận để cập nhật bằng ID
        Optional<FeedBack> optionalFeedback = feedBackRepository.findById(feedbackId);

        if (optionalFeedback.isPresent()) {
            FeedBack feedback = optionalFeedback.get();
            // Kiểm tra xem người dùng có quyền cập nhật bình luận không
            if (feedback.getCreateBy().equals(updatedBy)) {
                // Cập nhật nội dung và đánh giá mới
                feedback.setContent(newContent);
                feedback.setRate(newRate);
                // Lưu lại thời gian cập nhật
                feedback.setUpdateDate(new Date());
                return feedBackRepository.save(feedback);
            } else {
                throw new IllegalArgumentException("User does not have permission to update this feedback.");
            }
        } else {
            throw new IllegalArgumentException("Feedback not found with ID: " + feedbackId);
        }
    }

    @Override
    @Transactional
    public void deleteFeedback(int feedbackId, Integer deletedBy) {
        // Tìm kiếm bình luận để xóa bằng ID
        Optional<FeedBack> optionalFeedback = feedBackRepository.findById(feedbackId);

        if (optionalFeedback.isPresent()) {
            FeedBack feedback = optionalFeedback.get();
            // Kiểm tra xem người dùng có quyền xóa bình luận không
            if (feedback.getCreateBy().equals(deletedBy)) {
                // Xóa tất cả các bình luận con
                deleteChildFeedbacks(feedback);
                // Xóa bình luận cha khỏi cơ sở dữ liệu
                feedBackRepository.delete(feedback);
            } else {
                throw new IllegalArgumentException("User does not have permission to delete this feedback.");
            }
        } else {
            throw new IllegalArgumentException("Feedback not found with ID: " + feedbackId);
        }
    }

    // Hàm đệ quy để xóa tất cả các bình luận con
    private void deleteChildFeedbacks(FeedBack parentFeedback) {
        List<FeedBack> childFeedbacks = feedBackRepository.findByParentFeedback(parentFeedback);
        for (FeedBack childFeedback : childFeedbacks) {
            deleteChildFeedbacks(childFeedback); // Gọi đệ quy để xóa các bình luận con của bình luận con
            feedBackRepository.delete(childFeedback); // Xóa bình luận con khỏi cơ sở dữ liệu
        }
    }




}

