package com.example.backend.repository;

import com.example.backend.Enums.Status;
import com.example.backend.entity.Category;
import com.example.backend.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> getByName(String name);

    boolean existsByName(String name);

    List<Menu> findAllByStatusAndAndCategory(Status status, Category category);

    List<Menu> findAllByCategoryAndStatus(Category category, Status status);

    @Query("SELECT e FROM Menu e WHERE e.counter > 0 ORDER BY e.counter DESC")
    List<Menu> findAllTopStock();

    List<Menu> findTop3ByOrderByCounterDesc();




}
