package com.ecommerce.controller.order;

import com.ecommerce.command.order.OrderItemCommand;
import com.ecommerce.domain.payment.PaymentMethod;
import com.ecommerce.dto.order.OrderRequest;
import com.ecommerce.dto.order.OrderResponse;
import com.ecommerce.dto.order.OrderResult;
import com.ecommerce.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 주문 관련 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        Long customerId = orderRequest.getCustomerId();
        List<OrderItemCommand> orderItemCommands = orderRequest.toOrderItemCommands();
        PaymentMethod paymentMethod = orderRequest.getPaymentMethod();

        // 주문 생성
        OrderResult orderResult = orderService.order(customerId, orderItemCommands, paymentMethod);

        return OrderResponse.from(orderResult);
    }
}
