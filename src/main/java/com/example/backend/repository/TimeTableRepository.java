package com.example.backend.repository;

import com.example.backend.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface TimeTableRepository extends JpaRepository<TimeTable,Long> {
}

