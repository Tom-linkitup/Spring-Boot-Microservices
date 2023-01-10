package com.programming.orderservice.service;

import com.programming.orderservice.dto.OrderRequest;
import com.programming.orderservice.model.Order;
import com.programming.orderservice.model.OrderItem;
import com.programming.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void createOrder(OrderRequest orderRequest) {
        Order order = orderRequest.getOrder();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderItem> orderItemList = orderRequest.getOrderItemList();
        orderItemList.forEach(item -> order.add(item));
        orderRepository.save(order);
    }
}
