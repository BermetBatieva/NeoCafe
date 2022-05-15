package com.example.backend.dto;

import com.example.backend.entity.TimeTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class EmployeeForAdmin {
    private long id;
    private String name;
    private String role;
    private String branch;
    private String phoneNumber;
    private TimeTable timeTable;
    private List<Long> tablesId;
}
