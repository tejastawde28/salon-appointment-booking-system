package com.apb.notifications.impl;

import com.apb.notifications.dto.BookingDTO;
import com.apb.notifications.dto.NotificationDTO;
import com.apb.notifications.mapper.NotificationMapper;
import com.apb.notifications.model.Notification;
import com.apb.notifications.repository.NotificationRepository;
import com.apb.notifications.service.NotificationService;
import com.apb.notifications.service.client.BookingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final BookingFeignClient bookingFeignClient;

    @Override
    public NotificationDTO createNotification(Notification notification) throws Exception {
        Notification savedNotification = notificationRepository.save(notification);
        BookingDTO bookingDTO = bookingFeignClient.getBookingById(savedNotification.getBookingId()).getBody();
        NotificationDTO notificationDTO = NotificationMapper.toDTO(savedNotification, bookingDTO);
        return notificationDTO;
    }

    @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getAllNotificationsBySalonId(Long salonId) {
        return notificationRepository.findBySalonId(salonId);
    }

    @Override
    public Notification markNotificationAsRead(Long notificationId) throws Exception {
        return notificationRepository.findById(notificationId).map(
                notification -> {
                    notification.setRead(true);
                    return notificationRepository.save(notification);
                }
        ).orElseThrow(() -> new Exception("Notification not found"));

    }
}
