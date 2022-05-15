package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WaiterTableDto {

    private List<Long> tablesId;

    private String waiterPhoneNumber;
}
