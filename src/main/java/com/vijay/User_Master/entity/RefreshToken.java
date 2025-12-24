package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;
    private Instant expiryDate;

    private String username; // Added Username as Identifier
    private String email;

    @OneToOne
    private User user;

    @OneToOne
    private Worker worker;
}
