package com.example.backend.dto;
import com.example.backend.Enums.TypeGeneralAdditional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class GeneralAdditionalDTO {

    private long quantity;

    private long productId;

    private double price;

    private TypeGeneralAdditional typeGeneral;
}
