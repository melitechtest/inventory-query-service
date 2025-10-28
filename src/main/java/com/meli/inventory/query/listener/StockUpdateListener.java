package com.meli.inventory.query.listener;

import com.meli.inventory.query.dto.StockUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Listens for stock update events and persists the denormalized data to Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockUpdateListener {

    private final RedisTemplate<String, String> redisTemplate;
    public static final String QUEUE_NAME = "stock-query-queue";
    public static final String REDIS_KEY_PREFIX = "stock:";

    /**
     * Listens to the queue declared by the command service.
     *
     * @param event The received StockUpdateEvent object.
     */
    public void handleStockUpdate(StockUpdateEvent event) {
        log.info("Event received: {} -> {} units", event.getProductId(), event.getNewQuantity());
        String key = REDIS_KEY_PREFIX + event.getProductId();
        String value = String.valueOf(event.getNewQuantity());
        redisTemplate.opsForValue().set(key, value);
    }
}