package com.socialnetwork.socialnetwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String text;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_reply_owner")
    private User replyOwner;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_comment")
    private Comment comment;


}
