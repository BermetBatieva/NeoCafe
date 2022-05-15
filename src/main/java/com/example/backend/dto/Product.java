package com.example.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;

    private String productName;

    private double quantity;

    private long minNumber;

    private Date arrivalDate;

    private Date expirationDate;

    private Integer unit;

    private Long branchId;

    private Integer warehouseCategoryId;
}
