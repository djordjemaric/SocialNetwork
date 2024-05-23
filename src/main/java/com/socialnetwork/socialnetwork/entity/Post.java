package com.socialnetwork.socialnetwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean isPublic;

    private String text;

    @Column(name = "img_s3_key")
    private String imgS3Key;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_owner")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "id_group")
    private Group group;

    @OneToMany
    @JoinColumn(name = "id_post")
    private List<Comment> comments;

}
