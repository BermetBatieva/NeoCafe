package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuByID {
    private long id;
    private String menuName;
    private String image;
    private String description;
    private List<String> composition;
}
