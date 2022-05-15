package com.example.backend.repository;

import com.example.backend.entity.GeneralAdditional;
import com.example.backend.entity.Menu;
import com.example.backend.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GeneralAdditionalRepository extends JpaRepository<GeneralAdditional, Long> {

    List<GeneralAdditional> findAllByMenu(Menu menu);

    List<GeneralAdditional> findAllByWarehouse(Warehouse warehouse);
}
