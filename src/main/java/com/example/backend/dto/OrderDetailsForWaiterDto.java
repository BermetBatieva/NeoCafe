package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsForWaiterDto {
    private Long category;

    private Long orderDetailsId;

    private String nameMenu;

    private double menuPrice;//за шт

    private double totalOrderDetailsPrice;//кол*за шт

    private long quantity;

    private String image;

    private List<String> compositions;

}
