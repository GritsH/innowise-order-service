package com.grits.orderservice.kafka;

import com.grits.orderservice.dao.OrderDao;
import com.grits.orderservice.entity.Order;
import com.grits.orderservice.entity.status.OrderStatus;
import com.grits.orderservice.kafka.event.PaymentCreatedEvent;
import com.grits.orderservice.kafka.event.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaConsumer {

    private final OrderDao orderDao;

    @KafkaListener(
            topics = "${kafka.topic.create-payment}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handlePaymentCreated(PaymentCreatedEvent event) {
        log.info("Received Payment Event for orderId={} with status={}", event.getOrderId(), event.getStatus());

        Order order = orderDao.getOrderById(event.getOrderId());
        if (event.getStatus() == PaymentStatus.SUCCESS) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }
        orderDao.save(order);
    }
}
