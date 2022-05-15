package com.example.backend.dto;

import com.example.backend.Enums.OrderTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTypesDto {

    private OrderTypes orderTypes;
}
