package com.example.cinema_back_end.repositories;

import com.example.cinema_back_end.entities.Bill;
import com.example.cinema_back_end.entities.PinCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPinCodeRepository extends JpaRepository<PinCode, Integer> {
    Optional<PinCode> findByEmailAndPinAndContent(String username, String pinCode, String vertifyPin);
}