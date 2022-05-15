package com.example.backend.dto;

import com.example.backend.Enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TablesDtoForWaiter {

   private TableStatus status;
   private Long tableId;
}
