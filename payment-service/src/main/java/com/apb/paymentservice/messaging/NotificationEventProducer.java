package com.apb.paymentservice.messaging;


import com.apb.paymentservice.payload.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendNotification(Long bookingId, Long userId, Long salonId){
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setBookingId(bookingId);
        notificationDTO.setUserId(userId);
        notificationDTO.setSalonId(salonId);
        notificationDTO.setDescription("New Booking Confirmed");
        notificationDTO.setType("BOOKING");

        rabbitTemplate.convertAndSend("notification-queue", notificationDTO);
    }
}
