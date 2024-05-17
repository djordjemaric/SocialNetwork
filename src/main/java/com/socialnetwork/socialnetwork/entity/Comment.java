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
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String text;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_comm_owner")
    private User commOwner;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_post")
    private Post post;

    @OneToMany(mappedBy = "comment")
    private List<Reply> replies;

}
