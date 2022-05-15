package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MenuUpdateDTO {

    private Long id;

    private String name;

    private Long categoryId;

    private String image;

    private String description;

    private double price;

    private List<Long> deleteComposition;

    private List<Long> deleteGeneralAdditional;

    private List<CompositionDTO> composition;

    private List<GeneralAdditionalDTO> generalAdditional;
}
