package com.onlineshop.Online.shop.Repository;

import com.onlineshop.Online.shop.Model.Order;
import com.onlineshop.Online.shop.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);  // Pronalazi sve porudžbine za određenog korisnika
    List<Order> findByStatus(String status);  // Pronalazi sve porudžbine sa određenim statusom
}
