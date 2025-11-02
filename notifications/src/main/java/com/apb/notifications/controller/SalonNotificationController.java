package com.apb.notifications.controller;

import com.apb.notifications.dto.BookingDTO;
import com.apb.notifications.dto.NotificationDTO;
import com.apb.notifications.mapper.NotificationMapper;
import com.apb.notifications.model.Notification;
import com.apb.notifications.service.NotificationService;
import com.apb.notifications.service.client.BookingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications/salon-owner")
public class SalonNotificationController {

    private final NotificationService notificationService;
    private final BookingFeignClient bookingFeignClient;

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsBySalonId(@PathVariable Long salonId) {
        List<Notification> notifications = notificationService.getAllNotificationsBySalonId(salonId);
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(
                        notification -> {
                            try {
                                BookingDTO bookingDTO = bookingFeignClient.getBookingById(notification.getBookingId()).getBody();
                                return NotificationMapper.toDTO(notification, bookingDTO);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).toList();
        return ResponseEntity.ok(notificationDTOs);
    }

}
