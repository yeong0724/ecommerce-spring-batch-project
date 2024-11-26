package com.ecommerce.dto.order;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.payment.PaymentMethod;
import com.ecommerce.domain.payment.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResult {
    private Long orderId;

    private Timestamp orderDate;

    private Long customerId;

    private OrderStatus orderStatus;

    private List<OrderItemResult> orderItems;

    private Long productCount;

    private Long totalItemQuantity;

    private Long paymentId;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private Timestamp paymentDate;

    private Integer totalAmount;

    private boolean paymentSuccess;

    public static OrderResult from(Order order) {
        return new OrderResult(
                order.getOrderId(),
                order.getOrderDate(),
                order.getCustomerId(),
                order.getOrderStatus(),
                order.getOrderItems().stream().map(OrderItemResult::from).collect(Collectors.toList()),
                order.countProducts(),
                order.calculateTotalItemQuantity(),
                order.getPaymentId(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getPaymentDate(),
                order.calculateTotalAmount(),
                order.isPaymentSuccess()
        );
    }

    public static OrderResult of(
            Long orderId,
            Timestamp orderDate,
            Long customerId,
            OrderStatus orderStatus,
            List<OrderItemResult> orderItems,
            Long productCount,
            Long totalItemQuantity,
            Long paymentId,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            Timestamp paymentDate,
            Integer totalAmount,
            boolean paymentSuccess
    ) {
        return new OrderResult(
                orderId,
                orderDate,
                customerId,
                orderStatus,
                orderItems,
                productCount,
                totalItemQuantity,
                paymentId,
                paymentMethod,
                paymentStatus,
                paymentDate,
                totalAmount,
                paymentSuccess
        );
    }

}
