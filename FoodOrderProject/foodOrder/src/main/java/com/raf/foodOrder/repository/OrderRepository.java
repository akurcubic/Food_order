package com.raf.foodOrder.repository;

import com.raf.foodOrder.model.Order;
import com.raf.foodOrder.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByOrderByIdDesc();

    List<Order> findAllByOrderStatus(OrderStatus orderStatus);

    List<Order> findByOrderStatusIn(List<OrderStatus> statuses);
    List<Order> findByOrderStatusInAndUserId(List<OrderStatus> statuses, int userId);

    List<Order> findByUserIdOrderByIdDesc(int userId);

    @Query("SELECT COUNT(o) FROM ORDERS o WHERE o.orderStatus IN (:statuses)")
    long countActiveOrders(@Param("statuses") List<OrderStatus> statuses);

    List<Order> findAllByOrderStatusAndScheduledTimeBefore(OrderStatus status, LocalDateTime now);

}
