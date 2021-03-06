package com.example.backend.repository;

import com.example.backend.Enums.Status;
import com.example.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByStatus(Status status);

    boolean existsByName(String name);
}
