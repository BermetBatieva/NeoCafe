package com.example.backend.repository;

import com.example.backend.Enums.Status;
import com.example.backend.Enums.TableStatus;
import com.example.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<Tables, Long> {

    List<Tables> findAllByUser(User user);

    Optional<Tables> findByQrCode(String qr);

    boolean existsByQrCode(String qr_code);

    List<Tables> findAllByStatus(Status status);

    Tables findByUser_IdAndTableStatus(Long id, TableStatus tableStatus);

    Tables findByUser_IdAndId(Long id,long tableId);

    Tables findByIdAndUserAndTableStatus(Long id, User user,TableStatus tableStatus);
}
