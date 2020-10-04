package com.example.nectaronlinegrocery.model;

import com.google.firebase.Timestamp;

public class Order {
    String id, customerId, addressId, status;
    Double totalOrder;

    Timestamp timePlaced;

    public Order() {
    }

    public Order(String id, String customerId, String addressId, String status, Double totalOrder, Timestamp timePlaced) {
        this.id = id;
        this.customerId = customerId;
        this.addressId = addressId;
        this.status = status;
        this.totalOrder = totalOrder;
        this.timePlaced = timePlaced;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(Double totalOrder) {
        this.totalOrder = totalOrder;
    }

    public Timestamp getTimePlaced() {
        return timePlaced;
    }

    public void setTimePlaced(Timestamp timePlaced) {
        this.timePlaced = timePlaced;
    }
}
