package com.onlineshop.Online.shop.Controller;


import com.onlineshop.Online.shop.Model.Order;
import com.onlineshop.Online.shop.Service.OrderService;
import com.onlineshop.Online.shop.Service.OrderServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServiceInterface orderService;

    @Autowired
    private OrderController(OrderServiceInterface orderService)
    {
        this.orderService = orderService;
    }

    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Operation(summary = "Get order by ID", description = "Returns an order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @Operation(summary = "Create a new order", description = "Creates a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.saveOrder(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.OK);
    }

    @Operation(summary = "Delete an order", description = "Deletes an order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
