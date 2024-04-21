package com.example.cinema_back_end.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(length = 1000)
    private String smallImageURl;

    @Column(length = 500)
    private String shortDescription;

    @Column(length = 1000)
    private String longDescription;

    @Column(length = 1000)
    private String largeImageURL;

    private String director;

    private String actors;

    private String categories;

    private LocalDate releaseDate;

    private int duration;

    @Column(length = 1000)
    private String trailerURL;

    private String language;

    private String rated;

    private int isShowing;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<FeedBack> feedbacks = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "movie")
    private Set<UserMovieLikes> likedByUsers;
}
