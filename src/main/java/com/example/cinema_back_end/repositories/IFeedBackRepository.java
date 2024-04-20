package com.example.cinema_back_end.repositories;

import com.example.cinema_back_end.entities.FeedBack;
import com.example.cinema_back_end.entities.PinCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IFeedBackRepository extends JpaRepository<FeedBack, Integer> {
    List<FeedBack> findByParentFeedback(FeedBack parentFeedback);
}