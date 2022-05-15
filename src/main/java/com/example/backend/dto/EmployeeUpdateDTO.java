package com.example.backend.dto;

import com.example.backend.entity.TimeTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class EmployeeUpdateDTO {

    private Long id;
    private String name;
    private int roleId;
    private Date bDate;
    private String phoneNumber;
    private Long branchId;
    private TimeTable timeTable;
    private List<Long> tablesId;

}
