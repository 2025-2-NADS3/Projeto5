package br.com.menuux.comedoriadatia.Domain;

import java.io.Serializable;

public class CartItem implements Serializable {
    private ItemDomain item;
    private int quantity;
    private double totalPrice;

    public CartItem() {
    }

    public CartItem(ItemDomain item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.totalPrice = item.getPrice() * quantity;
    }

    public ItemDomain getItem() {
        return item;
    }

    public void setItem(ItemDomain item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = this.item.getPrice() * quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}