package com.example.cinema_back_end.dtos;

import com.example.cinema_back_end.entities.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@NoArgsConstructor
@Getter
@Setter
@Data
public class PinCodeDTO {
    private int id;
    private String email;

    private String pin;
}
