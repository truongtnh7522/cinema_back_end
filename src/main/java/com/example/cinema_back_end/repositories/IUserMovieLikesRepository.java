package com.example.cinema_back_end.repositories;

import com.example.cinema_back_end.entities.Movie;
import com.example.cinema_back_end.entities.Ticket;
import com.example.cinema_back_end.entities.User;
import com.example.cinema_back_end.entities.UserMovieLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUserMovieLikesRepository extends JpaRepository<UserMovieLikes, Integer> {
    @Query("SELECT uml.movie FROM UserMovieLikes uml WHERE uml.user = :user")
    List<Movie> findMoviesByUser(User user);
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserMovieLikes u WHERE u.user = :user AND u.movie = :movie")
    boolean existsByUserAndMovie(User user, Movie movie);

    UserMovieLikes findByUserAndMovie(User user, Movie movie);
}