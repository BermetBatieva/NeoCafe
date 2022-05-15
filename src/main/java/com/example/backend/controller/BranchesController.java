package com.example.backend.controller;

import com.example.backend.dto.BranchDto;
import com.example.backend.dto.BranchWorkingTimeDto;
import com.example.backend.entity.Branches;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.service.BranchTimeTableService;
import com.example.backend.service.BranchesService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/branches")
public class BranchesController {

    @Autowired
    private BranchesService branchesService;

    @Autowired
    private BranchTimeTableService branchTimeTableService;

    @ApiOperation("Получение полной информации о филиалах (на все 7 дней)")
    @GetMapping("/get-all-full")
    public List<Branches> getAll(){
        return branchesService.getAllForClient();
    }

    @ApiOperation("Получение сегодняшней информации о филиалах для клиента (используйте это)")
    @GetMapping("/get-all-up-to-date-info")
    public List<BranchWorkingTimeDto> getAllInfo(){
        return branchTimeTableService.getTodayInfo();
    }

}
