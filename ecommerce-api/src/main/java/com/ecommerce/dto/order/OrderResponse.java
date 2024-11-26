package com.ecommerce.dto.order;


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
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse {

    private Long orderId;
    private Timestamp orderDate;
    private Long customerId;
    private OrderStatus orderStatus;

    private List<OrderItemResponse> orderItems;
    private Long productCount;
    private Long totalItemQuantity;

    private Long paymentId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Timestamp paymentDate;
    private Integer totalAmount;
    private boolean paymentSuccess;

    public static OrderResponse from(OrderResult result) {
        return new OrderResponse(
                result.getOrderId(),
                result.getOrderDate(),
                result.getCustomerId(),
                result.getOrderStatus(),
                result.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList()),
                result.getProductCount(),
                result.getTotalItemQuantity(),
                result.getPaymentId(),
                result.getPaymentMethod(),
                result.getPaymentStatus(),
                result.getPaymentDate(),
                result.getTotalAmount(),
                result.isPaymentSuccess()
        );
    }

}
