package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchWorkingTimeDto {

    private long branchId;
    private String name;
    private String address;
    private String phoneNumber;
    private String link2gis;
    private String workingTime;

}
