package com.example.backend.entity;

import com.example.backend.Enums.Status;
import lombok.*;

import javax.persistence.*;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "branches")
public class Branches{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private  String name;

    @Column(name ="link_2_gis")
    private String link2gis;

    @Column(name ="phone_number")
    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne
    private BranchTimeTable workingTime;

}
