package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrdersDtoForBarista {
    private long id;
    private String clientName;
    private List<DetailsDTOForOrders> listOrderDetailsDto;
}
