package com.apb.notifications.mapper;

import com.apb.notifications.dto.BookingDTO;
import com.apb.notifications.dto.NotificationDTO;
import com.apb.notifications.model.Notification;

public class NotificationMapper {

    public static NotificationDTO toDTO(Notification notification, BookingDTO bookingDTO) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setType(notification.getType());
        notificationDTO.setDescription(notification.getDescription());
        notificationDTO.setRead(notification.isRead());
        notificationDTO.setUserId(notification.getUserId());
        notificationDTO.setSalonId(bookingDTO.getSalonId());
        notificationDTO.setCreatedAt(notification.getCreatedAt());

        return notificationDTO;
    }
}
