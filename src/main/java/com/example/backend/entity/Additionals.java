package com.example.backend.entity;


import com.example.backend.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "additionals")
public class Additionals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "additional_id")
    private long additionalId;

    @ManyToOne
    private GeneralAdditional generalAdditional;

    @ManyToOne
    private OrderDetails orderDetail;

}
