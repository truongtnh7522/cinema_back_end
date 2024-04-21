package com.example.cinema_back_end.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user_movie_likes")
public class UserMovieLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

}