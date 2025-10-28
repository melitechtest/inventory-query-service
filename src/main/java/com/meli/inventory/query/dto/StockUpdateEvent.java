package com.meli.inventory.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for stock updates received via JMS.
 * Fields must match the producer's event structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateEvent implements Serializable {
    private String productId;
    private int newQuantity;
}