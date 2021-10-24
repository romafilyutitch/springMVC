package com.epam.esm.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order extends Entity {
    private Double cost;
    private LocalDateTime orderDate;
    private Certificate certificate;

    public Order(Double cost, Certificate certificate) {
        super(null);
        this.cost = cost;
        this.certificate = certificate;
    }

    public Order(Long id, Double cost, LocalDateTime orderDate) {
        super(id);
        this.cost = cost;
        this.orderDate = orderDate;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
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
        return Objects.equals(cost, order.cost) && Objects.equals(orderDate, order.orderDate) && Objects.equals(certificate, order.certificate);
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
