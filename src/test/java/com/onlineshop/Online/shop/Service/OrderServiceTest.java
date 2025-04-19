package com.onlineshop.Online.shop.Service;

import com.onlineshop.Online.shop.Model.Order;
import com.onlineshop.Online.shop.Repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    private List<Order> orderList;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        //test data/given
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus("PENDING");
        testOrder.setTotalPrice(1000);

        orderList = new ArrayList<>();
        orderList.add(testOrder);

        //when
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.findAll()).thenReturn(orderList);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        //when
        List<Order> result = orderService.getAllOrders();

        //then
        assertEquals(1, result.size());
        verify(orderRepository,times(1)).findAll();
    }

    @Test
    void getOrderById_ShouldReturnOrderById() {
        //when
        Order result = orderService.getOrderById(1L);

        //then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository,times(1)).findById(1L);

    }

    @Test
    void getOrderById_WithWrongId_ShouldNotReturnOrderById() {
        //given
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        //when
        Order result = orderService.getOrderById(100L);

        //then
        assertNull(result);
        verify(orderRepository,times(1)).findById(100L);
    }

    @Test
    void saveOrder_ShouldReturnSavedOrder() {
        //when
        Order result = orderService.saveOrder(testOrder);

        //then
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(orderRepository,times(1)).save(any(Order.class));

    }

    @Test
    void deleteOrder() {
        //when
        orderService.deleteOrder(1L);

        //then
        verify(orderRepository,times(1)).deleteById(1L);

    }
}