package com.example.cinema_back_end.services;

import com.example.cinema_back_end.dtos.FeedBackDTO;
import com.example.cinema_back_end.dtos.MovieDTO;
import com.example.cinema_back_end.entities.FeedBack;
import com.example.cinema_back_end.entities.Movie;
import com.example.cinema_back_end.entities.User;
import com.example.cinema_back_end.entities.UserMovieLikes;
import com.example.cinema_back_end.repositories.IMovieRepository;
import com.example.cinema_back_end.repositories.IUserMovieLikesRepository;
import com.example.cinema_back_end.security.repo.IUserRepository;
import lombok.var;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieService implements IMovieService{

    @Autowired
    private IMovieRepository  movieRepository;
    @Autowired
    private IUserMovieLikesRepository userMovieLikesRepository;
    @Autowired
    private IUserRepository userRepository;
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
        // Lấy thông tin về người dùng hiện tại từ context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> currentUserOptional = userRepository.findByUsername(currentUsername);

        // Lấy thông tin về bộ phim từ cơ sở dữ liệu
        Optional<Movie> movieOptional = movieRepository.findById(movieId);
        if (movieOptional.isPresent() && currentUserOptional.isPresent()) {
            Movie movie = movieOptional.get();
            User currentUser = currentUserOptional.get();

            // Kiểm tra xem người dùng hiện tại đã like bộ phim hay chưa
            boolean isLikedByCurrentUser = userMovieLikesRepository.existsByUserAndMovie(currentUser, movie);

            // Ánh xạ thông tin từ đối tượng Movie sang MovieDTO
            MovieDTO movieDTO = modelMapper.map(movie, MovieDTO.class);
            movieDTO.setLikedByCurrentUser(isLikedByCurrentUser); // Đặt thông tin về việc like của người dùng hiện tại vào DTO

            return movieDTO;
        } else {
            return null; // hoặc xử lý nếu không tìm thấy bộ phim hoặc người dùng
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

    @Override
    public void likeMovie(Integer userId, Integer movieId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            Optional<Movie> movieOptional = movieRepository.findById(movieId);
            if (movieOptional.isPresent()) {
                User user = userOptional.get();
                Movie movie = movieOptional.get();
                UserMovieLikes userMovieLikes = new UserMovieLikes();
                userMovieLikes.setUser(user);
                userMovieLikes.setMovie(movie);
                userMovieLikesRepository.save(userMovieLikes);
            } else {
                throw new RuntimeException("Movie not found with ID: " + movieId);
            }
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }
    public List<Movie> getLikedMoviesByUser(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return userMovieLikesRepository.findMoviesByUser(user);
        }
        return null;
    }
}
