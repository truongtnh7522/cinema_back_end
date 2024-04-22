package com.example.cinema_back_end.dtos;

import com.example.cinema_back_end.entities.FeedBack;
import com.example.cinema_back_end.entities.Movie;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Data
public class FeedBackDTO {
    private int id;
    private Date createDate;
    private boolean isDeleted;
    private Integer createBy;
    private Date updateDate;
    private Integer updateBy;
    private String content;
    private Double rate;
    private String fullName;
    private List<FeedBackDTO> childFeedbacks;
    public List<FeedBackDTO> getChildFeedbacks() {
        return childFeedbacks;
    }

    public void setChildFeedbacks(List<FeedBackDTO> childFeedbacks) {
        this.childFeedbacks = childFeedbacks;
    }
}
