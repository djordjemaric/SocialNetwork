package com.socialnetwork.socialnetwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String text;

    @CreationTimestamp
    private LocalDateTime dateTimeAtCreation;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_comm_owner")
    private User commOwner;

    @OneToMany
    @JoinColumn(name = "id_comment")
    private List<Reply> replies;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_post")
    private Post post;


}