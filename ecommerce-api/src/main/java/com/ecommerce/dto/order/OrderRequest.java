package com.ecommerce.dto.order;

import com.ecommerce.command.order.OrderItemCommand;
import com.ecommerce.domain.payment.PaymentMethod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {
    private Long customerId;
    private List<OrderItemRequest> orderItems;
    private PaymentMethod paymentMethod;

    public static OrderRequest of(
            Long customerId,
            List<OrderItemRequest> orderItems,
            PaymentMethod paymentMethod
    ) {
        return new OrderRequest(customerId, orderItems, paymentMethod);
    }

    public List<OrderItemCommand> toOrderItemCommands() {
        return orderItems.stream()
                .map(item -> new OrderItemCommand(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());
    }
}