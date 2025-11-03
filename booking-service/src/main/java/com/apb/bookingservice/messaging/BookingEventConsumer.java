package com.apb.bookingservice.messaging;

import com.apb.bookingservice.model.PaymentOrder;
import com.apb.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final BookingService bookingService;

    @RabbitListener(queues = "booking-queue")
    public void bookingUpdateListener(PaymentOrder order) throws Exception {
        bookingService.confirmBooking(order);
    }
}
