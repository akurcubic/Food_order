package com.raf.foodOrder.controller;

import com.raf.foodOrder.model.*;
import com.raf.foodOrder.security.Authorize;
import com.raf.foodOrder.security.CheckSecurity;
import com.raf.foodOrder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @CheckSecurity(permissions = {"can_search_order"})
    public ResponseEntity<?> getAllOrders(){

        List<Order> orders = orderService.findAll();
        if(orders != null){
            return ResponseEntity.ok(orders);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}")
    @CheckSecurity(permissions = {"can_search_order"})
    public ResponseEntity<?> getOrdersForUser(@PathVariable int userId){

        List<Order> orders = orderService.ordersForUser(userId);
        if(orders != null){
            return ResponseEntity.ok(orders);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @CheckSecurity(permissions = {"can_place_order"}, message = "You cannot create order")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {

        Order newOrder = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }


    @PostMapping("/cancel_order/{orderId}")
    @CheckSecurity(permissions = {"can_cancel_order"}, message = "You cannot cancel order")
    public ResponseEntity<?> cancelOrder(@PathVariable int orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }


    //mora post zato sto front kada salje get ne moze da ima telo
    @PostMapping("/search_by_status")
    @CheckSecurity(permissions = {"can_search_order"})
    public ResponseEntity<?> getOrdersByStatus(@RequestBody SearchByStatusRequest listOfStatus){

        List<Order> orders = orderService.ordersByStatus(listOfStatus);
        return ResponseEntity.ok(orders);
    }

    @Authorize("ADMIN")
    @GetMapping("/search_by_user/{userId}")
    @CheckSecurity(permissions = {"can_search_order"})
    public ResponseEntity<?> getOrdersByUserId(@PathVariable String userId){

        List<Order> orders = orderService.ordersByUserId(userId);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/search_by_dates/")
    @CheckSecurity(permissions = {"can_search_order"})
    public List<Order> getOrdersBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return orderService.filterOrdersBetweenDates(startDate, endDate);
    }

    @PostMapping("/schedule")
    @CheckSecurity(permissions = {"can_schedule_order"})
    public ResponseEntity<Order> scheduleOrder(@RequestBody ScheduleOrderRequest scheduleOrderRequest) {

        Order scheduledOrder = orderService.scheduleOrder(scheduleOrderRequest);
        return ResponseEntity.ok(scheduledOrder);
    }

    @GetMapping("/dishes")
    @CheckSecurity(permissions = {"can_place_order"})
    public ResponseEntity<?> getAllDishes(){

        List<Dish> dishes = orderService.findAllDishes();
        if(dishes != null){
            return ResponseEntity.ok(dishes);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all_errors")
    public Page<ErrorMessage> getAllErrorMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getAllErrorMessages(page, size);
    }

    @GetMapping("/user_errors")
    public Page<ErrorMessage> getErrorMessagesByUserId(
            @RequestParam int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getErrorMessagesByUserId(userId, page, size);
    }
}
