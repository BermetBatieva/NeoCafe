package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class OrderDetailsDto {

    private List<Long> generalAdditionalId;

    private long stockId;

    private String name;

    private long quantity;

    private double price;

    private String urlImage;

}
