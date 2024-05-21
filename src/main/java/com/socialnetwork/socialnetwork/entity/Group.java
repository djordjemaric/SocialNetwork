package com.socialnetwork.socialnetwork.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"group\"")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_admin")
    private User admin;


    private boolean isPublic;

    @OneToMany(mappedBy = "group")
    private Set<GroupMember> members;

    @OneToMany(mappedBy = "group")
    private Set<GroupRequest> requests;


    @OneToMany(mappedBy = "group")
    private List<Post> posts;
}
