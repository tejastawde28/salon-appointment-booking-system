package com.apb.notifications.controller;


import com.apb.notifications.dto.BookingDTO;
import com.apb.notifications.dto.NotificationDTO;
import com.apb.notifications.impl.NotificationServiceImpl;
import com.apb.notifications.mapper.NotificationMapper;
import com.apb.notifications.model.Notification;
import com.apb.notifications.service.NotificationService;
import com.apb.notifications.service.client.BookingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final BookingFeignClient bookingFeignClient;

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody Notification notification) throws Exception {
        return ResponseEntity.ok(notificationService.createNotification(notification));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getAllNotificationsByUserId(userId);
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

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(@PathVariable Long notificationId) throws Exception {
        Notification notification = notificationService.markNotificationAsRead(notificationId);
        BookingDTO bookingDTO = bookingFeignClient.getBookingById(notification.getBookingId()).getBody();
        return ResponseEntity.ok(NotificationMapper.toDTO(notification,bookingDTO));
    }

}
