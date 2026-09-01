package com.grits.orderservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreatedEvent {

    private UUID paymentId;
    private UUID orderId;
    private PaymentStatus status;
}
