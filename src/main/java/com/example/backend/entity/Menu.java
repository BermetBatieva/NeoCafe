package com.example.backend.entity;

import com.example.backend.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
@AllArgsConstructor
@NoArgsConstructor
@Data


@Table(name = "menu")
@Entity

public class  Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private double price;

    private Long counter;

    private String imagesUrl;

    @ManyToOne
    private  Category category;

    @Enumerated(EnumType.STRING)
    private Status status;
}
