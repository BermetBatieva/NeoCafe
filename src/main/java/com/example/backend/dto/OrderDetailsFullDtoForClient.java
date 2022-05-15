package com.example.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailsFullDtoForClient {

    private Long id;

    private String name;

    private double price;

    private long quantity;

    private List<AdditionalForClientHistoryDto> chosenGeneralAdditional;

    private double calcTotal;
}
