package com.meli.inventory.query.listener;

import com.meli.inventory.query.dto.StockUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.jms.annotation.JmsListener; // <-- NEW IMPORT

/**
 * Listens for stock update events and persists the denormalized data to Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockUpdateListener {

    private final RedisTemplate<String, String> redisTemplate;
    public static final String REDIS_KEY_PREFIX = "stock:";

    private static final String STOCK_TOPIC = "stock.update.topic";

    /**
     * Listens to the Topic declared by the command service.
     * The containerFactory ensures Topic (Pub/Sub) mode is used.
     *
     * @param event The received StockUpdateEvent object.
     */
    @JmsListener(destination = STOCK_TOPIC, containerFactory = "jmsListenerContainerFactory")
    public void handleStockUpdate(StockUpdateEvent event) {
        log.info("Event received: {} -> {} units", event.getProductId(), event.getNewQuantity());

        String key = REDIS_KEY_PREFIX + event.getProductId();
        String value = String.valueOf(event.getNewQuantity());

        redisTemplate.opsForValue().set(key, value);
    }
}