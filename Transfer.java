package com.example.bank_backend.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientAccountNumber;
    private Double amount;
    private LocalDateTime date;

    @ManyToOne
    private User user;

}
