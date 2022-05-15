package com.example.backend.dto;

import com.example.backend.Enums.OrderStatus;
import com.example.backend.Enums.OrderTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCurrAndCompletedForClientDto {
    private long orderId;
    private Date orderTime;
    private OrderTypes orderType;
    private OrderStatus status;
    private Long tableId;
    private List<OrderDetailsFullDtoForClient> listOrderDetailsDto;
    private double totalPrice;

    private long branchId;
    private String branchName;
    private String branchAddress;
    private String branchPhoneNumber;
    private String branchLink2gis;
    private String branchWorkingTime;
}
