package com.morago.backend.repository;

import com.morago.backend.entity.Notification;
import com.morago.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByDateTimeDesc(User user);
    void deleteByUser(User user);
}
