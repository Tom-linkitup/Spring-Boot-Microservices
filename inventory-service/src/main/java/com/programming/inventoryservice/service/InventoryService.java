package com.programming.inventoryservice.service;

import com.programming.inventoryservice.dto.InventoryResponse;
import com.programming.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(Map<String, Integer> skuCodeWithQuantity) {

        List<String> skuCodes = skuCodeWithQuantity.keySet().stream().collect(toList());
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory ->
                    InventoryResponse.builder()
                           .skuCode(inventory.getSkuCode())
                           .isInStock(inventory.getQuantity() >= skuCodeWithQuantity.get(inventory.getSkuCode()))
                           .build()
                ).toList();
    }

}
