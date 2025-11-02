package com.apb.notifications.service;

import com.apb.notifications.dto.BookingDTO;
import com.apb.notifications.dto.NotificationDTO;
import com.apb.notifications.model.Notification;

import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(Notification notification) throws Exception;
    List<Notification> getAllNotificationsByUserId(Long userId);
    List<Notification> getAllNotificationsBySalonId(Long salonId);
    Notification markNotificationAsRead(Long notificationId) throws Exception;
}
