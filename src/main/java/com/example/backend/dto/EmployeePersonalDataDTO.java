package com.example.backend.dto;

import com.example.backend.entity.TimeTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class EmployeePersonalDataDTO {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date bDate;
    private TimeTable timeTable;

}
