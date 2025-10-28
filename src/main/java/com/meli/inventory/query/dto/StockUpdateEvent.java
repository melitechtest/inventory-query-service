package com.meli.inventory.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for stock updates received from RabbitMQ.
 * Must match the package structure and fields of the command service's DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateEvent implements Serializable {
    private String productId;
    private int newQuantity;
}