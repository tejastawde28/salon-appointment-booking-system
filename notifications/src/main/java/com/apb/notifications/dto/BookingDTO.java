package com.apb.notifications.dto;

import com.apb.notifications.domain.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDTO {
    private Long id;

    private Long salonId;;

    private Long customerId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Set<Long> serviceIds;

    private double totalPrice;

    private BookingStatus status = BookingStatus.PENDING;
}
