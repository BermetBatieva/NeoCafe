package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Note {
    private String subject;
    private String content;
    private Map<String, String> data;
    private String image;
}
