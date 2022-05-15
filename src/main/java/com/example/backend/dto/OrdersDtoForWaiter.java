package com.example.backend.dto;

import com.example.backend.Enums.OrderStatus;
import com.example.backend.Enums.OrderTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrdersDtoForWaiter {

    private Long id;

    private OrderStatus orderStatus;

    private Long tableId;

    private Date orderTime;

    private OrderTypes orderTypes;



}
