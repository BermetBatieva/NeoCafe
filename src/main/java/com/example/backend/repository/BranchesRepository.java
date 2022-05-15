package com.example.backend.repository;

import com.example.backend.Enums.Status;
import com.example.backend.entity.Branches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BranchesRepository extends JpaRepository<Branches,Long> {

    boolean existsByName(String name);

    Branches getByName(String name);

    List<Branches> findAllByStatus(Status status);
}
