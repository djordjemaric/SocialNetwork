package com.socialnetwork.socialnetwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

}
