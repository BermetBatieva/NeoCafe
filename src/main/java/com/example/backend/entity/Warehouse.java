package com.example.backend.entity;

import com.example.backend.Enums.EUnit;
import com.example.backend.Enums.EWarehouseCategory;
import com.example.backend.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    private double quantity;

    @Enumerated(EnumType.STRING)
    private EUnit unit;

    @Column(name = "minimum_number")
    private long minNumber;

    @Column(name = "arrival_date")
    private Date arrivalDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @ManyToOne
    private Branches branches;

    @Enumerated(EnumType.STRING)
    private EWarehouseCategory warehouseCategory;

    @Enumerated(EnumType.STRING)
    private Status status;
}
