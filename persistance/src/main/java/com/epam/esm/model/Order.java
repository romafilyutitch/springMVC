package com.epam.esm.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order extends Entity {
    private double cost;
    private LocalDateTime orderDate;
    private List<Certificate> certificates;

    public Order(Long id, double cost, LocalDateTime orderDate) {
        super(id);
        this.cost = cost;
        this.orderDate = orderDate;
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

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return Double.compare(order.cost, cost) == 0 && Objects.equals(orderDate, order.orderDate) && Objects.equals(certificates, order.certificates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cost, orderDate, certificates);
    }

    @Override
    public String toString() {
        return "Order{" +
                "cost=" + cost +
                ", orderDate=" + orderDate +
                ", certificates=" + certificates +
                '}';
    }
}
