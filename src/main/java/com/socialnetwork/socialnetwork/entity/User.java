package com.socialnetwork.socialnetwork.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @OneToMany(mappedBy = "admin")
    @JsonBackReference
    private List<Group> groups;

    @NotNull
    @OneToMany(mappedBy = "member")
    @JsonBackReference
    private List<GroupMember> groupMember;


    @NotNull
    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<GroupRequest> groupRequest;

}
