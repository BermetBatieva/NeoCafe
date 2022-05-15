package com.example.backend.dto;

import com.example.backend.Enums.TypeGeneralAdditional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class GeneralDTO {
    private Long id;
    private String nameProduct;
    private double price;
    private TypeGeneralAdditional typeGeneralAdditional;

}
