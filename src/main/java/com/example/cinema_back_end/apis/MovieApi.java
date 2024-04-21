package com.example.cinema_back_end.apis;

import com.example.cinema_back_end.dtos.MovieDTO;
import com.example.cinema_back_end.entities.Movie;
import com.example.cinema_back_end.repositories.IMovieRepository;
import com.example.cinema_back_end.security.repo.IUserRepository;
import com.example.cinema_back_end.services.IMovieService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(value="/api/movies", produces = "application/json")
public class MovieApi {
    @Autowired
    private IMovieService movieService;

    @Autowired
    private IMovieRepository movieRepository;
    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/showing")
    public ResponseEntity<List<MovieDTO>> findAllShowingMovies(){
        return new ResponseEntity<>(movieService.findAllShowingMovies(), HttpStatus.OK);
    }

    @GetMapping("/details")
    public MovieDTO getMovieById(@RequestParam Integer movieId){
        return movieService.getById(movieId);
    }

    @GetMapping("/showing/search")
    public List<MovieDTO> findAllShowingMoviesByName(@RequestParam String name){
        return movieService.findAllShowingMoviesByName(name);
    }

    @PostMapping
    public void updateMovie(@RequestBody Movie movie){
        movieRepository.save(movie);
    }

    @PostMapping("/like")
    public ResponseEntity<String> likeMovie(Principal principal,
                                            @RequestParam("movieId") Integer movieId) {
        String username = principal.getName();
        Integer userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username))
                .getId();
        movieService.likeMovie(userId, movieId);
        return ResponseEntity.status(HttpStatus.OK).body("Movie liked successfully.");
    }

    @GetMapping("/liked-by-user")
    public ResponseEntity<List<Movie>> getLikedMoviesByUser(Principal principal) {
        String username = principal.getName();
        Integer userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username))
                .getId();
        List<Movie> likedMovies = movieService.getLikedMoviesByUser(userId);
        if (likedMovies != null) {
            return ResponseEntity.status(HttpStatus.OK).body(likedMovies);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<MovieDTO>> getRecommendedMovies() {
        List<MovieDTO> recommendedMovies = movieService.findRecommendedMovies();
        return ResponseEntity.ok(recommendedMovies);
    }
}
