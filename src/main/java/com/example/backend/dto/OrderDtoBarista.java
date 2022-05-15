package com.example.backend.dto;

import com.example.backend.Enums.OrderTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDtoBarista {
    private OrderTypes orderType;
    private List<OrderDetailsDto> listOrderDetailsDto;

}
