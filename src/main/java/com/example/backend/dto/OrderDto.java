package com.example.backend.dto;

import com.example.backend.Enums.OrderTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private OrderTypes orderType;
    private Long branchId;
    private Long tableId;
    private List<OrderDetailsDto> listOrderDetailsDto;

}
