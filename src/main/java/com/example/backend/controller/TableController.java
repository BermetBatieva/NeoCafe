package com.example.backend.controller;


import com.example.backend.dto.TableDTO;
import com.example.backend.dto.TableDTOid;
import com.example.backend.dto.TablesDtoForWaiter;
import com.example.backend.service.TableService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("table")
public class TableController {
    
    @Autowired
    private TableService tableService;

    @ApiOperation(value = "получение столов текущего пользователя(официанта)")
    @GetMapping("get-tables-for-current-user")
    public List<TablesDtoForWaiter> getTablesForCurrentUser(){
        return tableService.allTablesByUser();
    }

    @GetMapping("/is-free/{id}")
    public boolean getTableStatus(@PathVariable(value = "id") Long id) throws Exception {
        return tableService.isFree(id);
    }
}