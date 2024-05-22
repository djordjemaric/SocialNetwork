package com.socialnetwork.socialnetwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

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
    private String imgS3Url;

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
