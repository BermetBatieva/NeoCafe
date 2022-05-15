package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuForBarista {
    private Long id;
    private String name;
    private String image;
    private double price;
    private List<String> composition;
    private List<GeneralDTO> generalList;
    private boolean flag;
}
