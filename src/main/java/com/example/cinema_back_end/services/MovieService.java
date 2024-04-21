package com.example.cinema_back_end.services;

import com.example.cinema_back_end.dtos.FeedBackDTO;
import com.example.cinema_back_end.dtos.MovieDTO;
import com.example.cinema_back_end.entities.FeedBack;
import com.example.cinema_back_end.entities.Movie;
import com.example.cinema_back_end.repositories.IMovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieService implements IMovieService{

    @Autowired
    private IMovieRepository  movieRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public List<MovieDTO> findAllShowingMovies() {
        return movieRepository.findMoviesByIsShowingOrderByIdDesc(1)
                .stream()
                .map(movie -> modelMapper.map(movie, MovieDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public MovieDTO getById(Integer movieId) {
        Movie movie = movieRepository.getById(movieId);
        if (movie != null) {
            // Ánh xạ thông tin từ đối tượng Movie sang MovieDTO
            MovieDTO movieDTO = modelMapper.map(movie, MovieDTO.class);
            // Lấy danh sách các phản hồi của bộ phim
            List<FeedBackDTO> topLevelFeedbackDTOs = getTopLevelFeedbackDTOs(movie.getFeedbacks());
            // Ánh xạ thông tin về phản hồi và phản hồi con vào MovieDTO
            movieDTO.setFeedbacks(topLevelFeedbackDTOs);
            return movieDTO;
        } else {
            return null; // hoặc xử lý nếu không tìm thấy bộ phim
        }
    }

    private List<FeedBackDTO> getTopLevelFeedbackDTOs(Set<FeedBack> feedbacks) {
        List<FeedBackDTO> topLevelFeedbackDTOs = new ArrayList<>();
        for (FeedBack feedback : feedbacks) {
            // Check if the feedback has no parent_feedback_id (top-level feedback)
            if (feedback.getParentFeedback() == null) {
                FeedBackDTO feedbackDTO = modelMapper.map(feedback, FeedBackDTO.class);
                // Recursive call to get child feedbacks
                List<FeedBackDTO> childFeedbackDTOs = getChildFeedbackDTOs(feedback.getChildFeedbacks());
                feedbackDTO.setChildFeedbacks(childFeedbackDTOs);
                topLevelFeedbackDTOs.add(feedbackDTO);
            }
        }
        return topLevelFeedbackDTOs;
    }

    private List<FeedBackDTO> getChildFeedbackDTOs(Set<FeedBack> feedbacks) {
        List<FeedBackDTO> childFeedbackDTOs = new ArrayList<>();
        for (FeedBack feedback : feedbacks) {
            FeedBackDTO feedbackDTO = modelMapper.map(feedback, FeedBackDTO.class);
            // Recursive call to get child feedbacks
            List<FeedBackDTO> grandChildFeedbackDTOs = getChildFeedbackDTOs(feedback.getChildFeedbacks());
            feedbackDTO.setChildFeedbacks(grandChildFeedbackDTOs);
            childFeedbackDTOs.add(feedbackDTO);
        }
        return childFeedbackDTOs;
    }

    @Override
    public List<MovieDTO> findAllShowingMoviesByName(String keyword) {
        return movieRepository.findMoviesByIsShowingAndNameContaining(1,keyword)
                .stream().map(movie -> modelMapper.map(movie,MovieDTO.class))
                .collect(Collectors.toList());
    }
}
