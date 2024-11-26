package com.ecommerce.domain.order;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private Integer quantity;

    private Integer unitPrice;

    private String productId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private Order order;

    private OrderItem(Integer quantity, Integer unitPrice, String productId, Order order) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.productId = productId;
        this.order = order;
    }

    public static OrderItem createOrderItem(
            String productId,
            Integer quantity,
            Integer unitPrice,
            Order order
    ) {
        return new OrderItem(quantity, unitPrice, productId, order);
    }
}
