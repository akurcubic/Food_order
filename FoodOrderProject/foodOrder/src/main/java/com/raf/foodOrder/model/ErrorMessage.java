package com.raf.foodOrder.model;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class ErrorMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    @NotNull
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "orderId", referencedColumnName = "id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;
    @Column
    @NotNull
    private String operations;
    @Column
    @NotNull
    private String message;


    public ErrorMessage(){


    }

}
