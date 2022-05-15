package com.example.backend.repository;

import com.example.backend.entity.Composition;
import com.example.backend.entity.Menu;
import com.example.backend.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompositionRepository extends JpaRepository<Composition, Long> {

    List<Composition> findAllByMenu(Menu menu);

    List<Composition> findAllByProductId(Warehouse w);
}
