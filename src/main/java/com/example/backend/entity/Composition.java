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
@Entity
@Table(name = "composition")
public class Composition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Menu menu;

    @ManyToOne
    private Warehouse productId;

    private long quantity;

    @Enumerated(EnumType.STRING)
    private Status status;
}
