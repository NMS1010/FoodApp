package com.ms.food_app.models;

import java.io.Serializable;
import java.util.List;

public class Delivery  implements Serializable {
    private long id;
    private String name;

    private double price;

    private String description;

    private boolean isDeleted = false;

    private List<Order> orders;
}
