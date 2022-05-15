package com.example.backend.dto;

import com.example.backend.Enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangeStatusDto {

    private long orderId;
    private OrderStatus orderStatus;
}
