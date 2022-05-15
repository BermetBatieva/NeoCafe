package com.example.backend.repository;

import com.example.backend.entity.Notifications;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

    List<Notifications> findAllByUser(User user);
    void deleteAllByUser(User user);
}
