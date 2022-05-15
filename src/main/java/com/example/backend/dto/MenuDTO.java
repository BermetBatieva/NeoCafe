package com.example.backend.dto;

import com.example.backend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MenuDTO {

    private Long id;

    private String name;

    private long categoryId;;

    private String description;

    private double price;

    private List<CompositionDTO> composition;

    private List<GeneralAdditionalDTO> generalAdditional;
}
