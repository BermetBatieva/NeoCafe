package com.example.backend.controller;

import com.example.backend.dto.MenuForWaiter;
import com.example.backend.entity.Menu;
import com.example.backend.service.MenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "Получить все блюда по выбранной (id)категории")
    @GetMapping("/get-all/{id}")
    public List<Menu> getAllByCategory(@PathVariable Long id) throws Exception {
        return menuService.getAllStocksByCategory(id);
    }


    @ApiOperation(value = "получение всех блюд по категории(waiter)")
    @GetMapping("get-all-byCategory/{categoryId}")
    public List<MenuForWaiter> getAllByCategoryIdForWaiter(@PathVariable long categoryId) throws Exception {
        return menuService.allMenuByCategoryForWaiter(categoryId);
    }

}
