package com.example.backend.repository;

import com.example.backend.Enums.EWarehouseCategory;
import com.example.backend.Enums.Status;
import com.example.backend.entity.Menu;
import com.example.backend.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository <Warehouse,Long>{

    Optional<Warehouse> findByProductName(String name);

    Optional<Warehouse> findByProductId(Long id);

    boolean existsByProductName(String name);

    List<Warehouse> findAllByStatus(Status status);

    List<Warehouse> findAllByStatusAndWarehouseCategory(Status activate, EWarehouseCategory eWarehouseCategory);

    @Query("SELECT e FROM Warehouse e WHERE e.quantity<e.minNumber and e.status='ACTIVATE'")
    List<Warehouse> findAllEndingProducts();

}
