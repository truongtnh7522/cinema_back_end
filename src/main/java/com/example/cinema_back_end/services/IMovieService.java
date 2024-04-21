package com.example.cinema_back_end.services;


import com.example.cinema_back_end.dtos.MovieDTO;
import com.example.cinema_back_end.entities.Movie;
import com.example.cinema_back_end.entities.User;


import java.util.List;

public interface IMovieService {
    List<MovieDTO> findAllShowingMovies();
    MovieDTO getById(Integer movieId);
    List<MovieDTO> findAllShowingMoviesByName(String name);
    public List<Movie> getLikedMoviesByUser(Integer userId);
    void likeMovie(Integer userId, Integer movieId);
}
