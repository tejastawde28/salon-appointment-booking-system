package com.apb.notifications.messaging;

import com.apb.notifications.model.Notification;
import com.apb.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification-queue")
    private void notificationEventConsumer(Notification notification) throws Exception {
        notificationService.createNotification(notification);
    }
}
