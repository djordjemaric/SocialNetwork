package com.socialnetwork.socialnetwork.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Friends {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_friend1")
    private User friendTo;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_friend2")
    private User friend;
}
