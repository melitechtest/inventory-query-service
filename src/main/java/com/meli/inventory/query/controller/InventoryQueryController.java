package com.meli.inventory.query.controller;

import com.meli.inventory.query.listener.StockUpdateListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/queries")
@RequiredArgsConstructor
public class InventoryQueryController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/stock/{productId}")
    public ResponseEntity<?> getStock(@PathVariable String productId) {
        final String key = StockUpdateListener.REDIS_KEY_PREFIX + productId;

        try {
            String stockValue = redisTemplate.opsForValue().get(key);

            if (stockValue == null) {
                return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofSeconds(5))).body(Map.of("productId", productId, "stock", 0, "status", "Not yet processed or zero stock"));
            }

            Integer stock = safeParseInt(stockValue);
            if (stock == null) {
                log.warn("Non-numeric stock value in cache for key {}: '{}'", key, stockValue);
                return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(Map.of("productId", productId, "stock", 0, "status", "Invalid cached value; defaulted to 0"));
            }

            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(Duration.ofSeconds(5))).body(Map.of("productId", productId, "stock", stock));

        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure while reading {}", key, e);
            return ResponseEntity.status(503).cacheControl(CacheControl.noCache()).body(Map.of("error", "Cache temporarily unavailable", "productId", productId));
        } catch (DataAccessException e) {
            log.error("DataAccessException while reading {}", key, e);
            return ResponseEntity.status(502).cacheControl(CacheControl.noCache()).body(Map.of("error", "Cache access error", "productId", productId));
        } catch (Exception e) {
            log.error("Unexpected error in getStock for {}", productId, e);
            return ResponseEntity.internalServerError().cacheControl(CacheControl.noCache()).body(Map.of("error", "Unexpected error", "productId", productId));
        }
    }

    private Integer safeParseInt(String value) {
        try {
            return Integer.valueOf(value.trim());
        } catch (Exception ignored) {
            return null;
        }
    }
}