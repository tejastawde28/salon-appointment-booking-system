package com.apb.bookingservice.service;

import com.apb.bookingservice.domain.BookingStatus;
import com.apb.bookingservice.dto.BookingRequest;
import com.apb.bookingservice.dto.SalonDTO;
import com.apb.bookingservice.dto.ServiceDTO;
import com.apb.bookingservice.dto.UserDTO;
import com.apb.bookingservice.model.Booking;
import com.apb.bookingservice.model.PaymentOrder;
import com.apb.bookingservice.model.SalonReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookingService {

    Booking createBooking(BookingRequest booking, UserDTO user, SalonDTO salon, Set<ServiceDTO> services) throws Exception;
    List<Booking> getBookingsByCustomer(Long customerId);
    List<Booking> getBookingsBySalon(Long salonId);
    Booking getBookingById(Long id) throws Exception;
    Booking updateBooking(Long bookingId, BookingStatus status) throws Exception;
    List<Booking> getBookingsByDate(LocalDate date, Long salonId);
    SalonReport getSalonReport(Long salonId);
    void deleteBooking(Long bookingId) throws Exception;
    void confirmBooking(PaymentOrder order) throws Exception;
}
