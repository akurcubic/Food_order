package com.raf.foodOrder.model;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    @NotNull
    private String name;


    public Dish(){

    }

    public Dish(String name) {
        this.name = name;
    }
}
