package com.epam.esm.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Order extends Entity {
    private double cost;
    private LocalDateTime orderDate;
    private Certificate certificate;

    public Order(Long id, double cost, LocalDateTime orderDate, Certificate certificate) {
        super(id);
        this.cost = cost;
        this.orderDate = orderDate;
        this.certificate = certificate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return Double.compare(order.cost, cost) == 0 && Objects.equals(orderDate, order.orderDate) && Objects.equals(certificate, order.certificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cost, orderDate, certificate);
    }

    @Override
    public String toString() {
        return "Order{" +
                "cost=" + cost +
                ", orderDate=" + orderDate +
                ", certificate=" + certificate +
                '}';
    }
}
