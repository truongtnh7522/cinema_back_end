package com.example.cinema_back_end.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Feedback")
@Getter
@Setter
@NoArgsConstructor
public class FeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "create_by")
    private Integer createBy;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "update_by")
    private Integer updateBy;

    @Column(name = "content")
    private String content;

    @Column(name = "rate")
    private Double rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = true)
    @JsonBackReference
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_feedback_id", nullable = true)
    @JsonBackReference
    private FeedBack parentFeedback;

    @OneToMany(mappedBy = "parentFeedback", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<FeedBack> childFeedbacks = new HashSet<>();

}
