package com.ms.food_app.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private long id;
    private String address;

    private String phone;

    private String status;

    private boolean paidBefore = false;

    private double amountFromUser;

    private User user;

    private Delivery delivery;

    private List<OrderItem> orderItems;
    private Date createdAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPaidBefore() {
        return paidBefore;
    }

    public void setPaidBefore(boolean paidBefore) {
        this.paidBefore = paidBefore;
    }

    public double getAmountFromUser() {
        return amountFromUser;
    }

    public void setAmountFromUser(double amountFromUser) {
        this.amountFromUser = amountFromUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
