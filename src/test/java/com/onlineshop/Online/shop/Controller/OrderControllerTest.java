package com.onlineshop.Online.shop.Controller;

import com.onlineshop.Online.shop.Service.OrderServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import com.onlineshop.Online.shop.Model.Order;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderServiceInterface orderServiceInterface;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;
    private List<Order> orderList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //given
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus("PENDING");
        testOrder.setTotalPrice(100.0);

        orderList = new ArrayList<>();
        orderList.add(testOrder);

        //when
        when(orderServiceInterface.getAllOrders()).thenReturn(orderList);
        when(orderServiceInterface.getOrderById(1L)).thenReturn(testOrder);
        when(orderServiceInterface.saveOrder(any(Order.class))).thenReturn(testOrder);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        //when
        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(orderServiceInterface, times(1)).getAllOrders();
    }

    @Test
    void getOrderById_ShouldReturnOrderById() {
        //when
        ResponseEntity<Order> response = orderController.getOrderById(1L);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(orderServiceInterface, times(1)).getOrderById(1L);
    }

    @Test
    void createOrder_ShouldCreateOrder() {

        //given
        Order newOrder = new Order();
        newOrder.setStatus("NEW");
        newOrder.setTotalPrice(200);

        //when
        ResponseEntity<Order> response = orderController.createOrder(newOrder);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderServiceInterface, times(1)).saveOrder(newOrder);
    }

    @Test
    void deleteOrder_ShouldDeleteOrder() {
        //when
        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        //then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderServiceInterface, times(1)).deleteOrder(1L);
    }
}