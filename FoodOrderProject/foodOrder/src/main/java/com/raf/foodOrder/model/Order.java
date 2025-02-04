package com.raf.foodOrder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ORDERS")
@Data

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column
    @NotNull
    private boolean active;
    @Column
    @NotNull
    private LocalDateTime createdDate;

    private LocalDateTime preparingTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime scheduledTime;

    @Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "createdBy", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<ErrorMessage> errorMessages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ORDER_DISH",
            joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id", referencedColumnName = "id")
    )
    private List<Dish> items = new ArrayList<>();

    public Order() {

    }

    @PrePersist
    public void prePersist() {
        if (orderStatus == null) {
            this.orderStatus = OrderStatus.ORDERED;
        }
        this.active = true;
        this.createdDate = LocalDateTime.now();
    }

}

