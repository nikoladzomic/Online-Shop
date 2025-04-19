package com.onlineshop.Online.shop.Service;


import com.onlineshop.Online.shop.Model.Order;
import com.onlineshop.Online.shop.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements OrderServiceInterface{

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository){

        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id)
    {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order saveOrder(Order order)
    {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id)
    {
        orderRepository.deleteById(id);
    }

}
