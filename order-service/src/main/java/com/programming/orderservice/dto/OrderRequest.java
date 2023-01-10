package com.programming.orderservice.dto;

import com.programming.orderservice.model.Order;
import com.programming.orderservice.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private Order order;
    private List<OrderItem> orderItemList;

}
