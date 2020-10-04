package com.example.nectaronlinegrocery.model;

import java.util.List;

public class CartItems {
    private List<Item> itemDetails;
    private Integer quantity;
    private Double totalPrice;

    public CartItems() {
    }

    public CartItems(List<Item> itemDetails, Integer quantity, Double totalPrice) {
        this.itemDetails = itemDetails;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public List<Item> getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(List<Item> itemDetails) {
        this.itemDetails = itemDetails;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
