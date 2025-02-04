package com.raf.foodOrder.model;

import lombok.Data;

@Data
public class ScheduleOrderRequest {

    private String scheduledTime;
    private String dishes;
}
