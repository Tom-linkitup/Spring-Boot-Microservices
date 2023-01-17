package com.programming.orderservice.service;

import com.programming.orderservice.dto.InventoryResponse;
import com.programming.orderservice.dto.OrderRequest;
import com.programming.orderservice.model.Order;
import com.programming.orderservice.model.OrderItem;
import com.programming.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, WebClient webClient) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
    }

    public void createOrder(OrderRequest orderRequest) {
        Order order = orderRequest.getOrder();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderItem> orderItemList = orderRequest.getOrderItemList();

        Map<String, Integer> skuCodeWithQuantity = orderItemList.stream()
                .collect(Collectors.toMap(OrderItem::getSkuCode, OrderItem::getQuantity));

        // call inventory service to check the item stock
        InventoryResponse[] inventoryResponses = webClient.post()
                .uri("http://localhost:8082/api/inventory")
                .body(BodyInserters.fromValue(skuCodeWithQuantity))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean result = Arrays.asList(inventoryResponses).stream().allMatch(InventoryResponse::isInStock);

        if (!result) {
            throw new IllegalArgumentException("Product is out of stock!");
        }

        orderItemList.forEach(item -> order.add(item));
        orderRepository.save(order);
    }
}
