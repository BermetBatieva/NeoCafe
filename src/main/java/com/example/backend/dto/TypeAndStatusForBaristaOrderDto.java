package com.example.backend.dto;

import com.example.backend.Enums.OrderStatus;
import com.example.backend.Enums.OrderTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TypeAndStatusForBaristaOrderDto {

    private OrderStatus orderStatus;
    private OrderTypes orderTypes;
}
