package com.digitalsmartclass.repository;
import com.digitalsmartclass.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.math.BigDecimal;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
