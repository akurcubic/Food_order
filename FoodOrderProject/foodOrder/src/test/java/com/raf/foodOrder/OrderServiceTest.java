package com.raf.foodOrder;

import com.raf.foodOrder.model.Order;
import com.raf.foodOrder.model.OrderStatus;
import com.raf.foodOrder.repository.OrderRepository;
import com.raf.foodOrder.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testOptimisticLocking() throws InterruptedException {

        Order order = new Order();
        order.setActive(true);
        order.setOrderStatus(OrderStatus.ORDERED);
        order = orderRepository.save(order);

        Order finalOrder = order;
        Thread userThread = new Thread(() -> {
            try {
                orderService.cancelOrder(finalOrder.getId());
            } catch (Exception e) {
                System.out.println("User could not cancel order: " + e.getMessage());
            }
        });

        Order finalOrder1 = order;
        Thread adminThread = new Thread(() -> {
            try {

                Thread.sleep(100);
                orderService.cancelOrder(finalOrder1.getId());
            } catch (Exception e) {
                System.out.println("Admin could not cancel order: " + e.getMessage());
            }
        });

        userThread.start();
        adminThread.start();

        userThread.join();
        adminThread.join();

        Optional<Order> updatedOrder = orderRepository.findById(order.getId());
        assertTrue(updatedOrder.isPresent(), "Order should exist");
        assertEquals(OrderStatus.CANCELED, updatedOrder.get().getOrderStatus(), "Order status should be CANCELED");
    }
}
