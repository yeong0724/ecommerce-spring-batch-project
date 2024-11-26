package com.ecommerce.service.order;

import com.ecommerce.command.order.OrderItemCommand;
import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.payment.PaymentMethod;
import com.ecommerce.dto.order.OrderResult;
import com.ecommerce.dto.product.ProductResult;
import com.ecommerce.repository.order.OrderRepository;
import com.ecommerce.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final ProductService productService;

    // 주문 생성
    @Transactional
    public OrderResult order(Long customerId, List<OrderItemCommand> orderItems, PaymentMethod paymentMethod) {
        Order order = Order.createOrder(customerId);
        for (OrderItemCommand item : orderItems) {
            ProductResult product = productService.findProduct(item.getProductId());
            order.addOrderItem(product.getProductId(), item.getQuantity(),
                    product.getSalesPrice());
        }
        order.initPayment(paymentMethod);
        return save(order);
    }

    private OrderResult save(Order order) {
        Order result = orderRepository.save(order);
        return OrderResult.from(result);
    }
}
