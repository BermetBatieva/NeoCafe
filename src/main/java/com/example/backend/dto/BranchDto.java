package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchDto {

    private Long id;

    private String name;

    private String address;

    private String phoneNumber;

    private String link2gis;

    private BranchTimeDTO branchTimeDTO;

}
