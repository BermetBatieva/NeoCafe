package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data

public class OrdersHistoryForClient {

    private String branchName;

    private List<HistoryForClient>  menuDTOForHistories;
}
