package com.example.cinema_back_end.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "pin_code")
@NoArgsConstructor
@Getter
@Setter
public class PinCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "pin")
    private String pin;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "content")
    private String content;

    // Constructors, getters, and setters
}