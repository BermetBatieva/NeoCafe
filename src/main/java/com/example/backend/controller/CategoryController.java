package com.example.backend.controller;

import com.example.backend.dto.CategoryDto;
import com.example.backend.entity.Category;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "Получение всех категорий меню")
    @GetMapping("/all")
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @ApiOperation(value = "Добавление новой категории")
    @PostMapping("/add")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) throws AlreadyExistsException {
        return new ResponseEntity<>(categoryService.add(categoryDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Удаление категории")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @ApiOperation(value = "Изменение категории")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> edit(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.updateCategory(id, categoryDto),HttpStatus.OK);
    }
}
