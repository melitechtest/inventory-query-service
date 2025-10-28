package com.meli.inventory.query.controller;

import com.meli.inventory.query.listener.StockUpdateListener;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for handling read queries from the Redis cache.
 */
@RestController
@RequestMapping("/api/queries")
@RequiredArgsConstructor
public class InventoryQueryController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/stock/{productId}")
    public ResponseEntity<?> getStock(@PathVariable String productId) {
        String key = StockUpdateListener.REDIS_KEY_PREFIX + productId;
        String stockValue = redisTemplate.opsForValue().get(key);

        if (stockValue == null) {
            return ResponseEntity.ok(Map.of("productId", productId, "stock", 0, "status", "Not yet processed or zero stock"));
        }

        return ResponseEntity.ok(Map.of("productId", productId, "stock", Integer.parseInt(stockValue)));
    }
}