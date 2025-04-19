package com.onlineshop.Online.shop.Service;

import com.onlineshop.Online.shop.Model.Order;

import java.util.List;

public interface OrderServiceInterface {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order saveOrder(Order order);
    void deleteOrder(Long id);
}
