package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data

public class HistoryForClient {

    private Long id;
    private String name;
    private double price;
    private double quantity;

}
