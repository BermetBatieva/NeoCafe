package com.example.backend.dto;

import com.example.backend.entity.TimeTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class EmployeeDto {

    private Long id;

    private String phoneNumber;

    private Long branchId;

    private  Integer roleId;

    private TimeTable timeTable;

    private List<Long> tableId;

}
