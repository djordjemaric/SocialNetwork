package com.socialnetwork.socialnetwork.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    @JsonManagedReference
    private User admin;

//    @NotNull
//    @OneToMany(mappedBy = "group")
//    @JsonBackReference
//    private List<GroupMember> groupMember;


//    @OneToMany(mappedBy = "group")
//    @JsonBackReference
//    private List<GroupRequest> groupRequests;


    private boolean isPublic;



}

