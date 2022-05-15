package com.example.backend.service;

import com.example.backend.Enums.Status;
import com.example.backend.dto.CategoryDto;
import com.example.backend.entity.Category;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAllByStatus(Status.ACTIVATE);
    }

    @Transactional
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("категория с такой id  не существует! id = ", id)
        );
        category.setStatus(Status.DELETED);
        categoryRepository.save(category);
    }

    public CategoryDto add(CategoryDto categoryDto) throws AlreadyExistsException {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new AlreadyExistsException("Категория с таким именем уже существует!");
        } else {
            Category category = new Category();
            category.setName(categoryDto.getName());
            category.setStatus(Status.ACTIVATE);
            categoryRepository.save(category);
            return categoryDto;
        }
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("категория с такой id  не существует! id = ", id)
        );
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return categoryDto;
    }
}
