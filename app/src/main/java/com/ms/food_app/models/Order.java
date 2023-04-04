package com.ms.food_app.models;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String address;

    private String phone;

    private String status;

    private boolean isPaidBefore = false;

    private double amountFromUser;

    private long userId;

    private Delivery delivery;

    private List<OrderItem> orderItems;
}
