package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@Data

public class DetailsDTOForOrders {
    private String name;
    private double price;
    private String urlImage;
}
