package com.apb.bookingservice.impl;

import com.apb.bookingservice.domain.BookingStatus;
import com.apb.bookingservice.dto.BookingRequest;
import com.apb.bookingservice.dto.SalonDTO;
import com.apb.bookingservice.dto.ServiceDTO;
import com.apb.bookingservice.dto.UserDTO;
import com.apb.bookingservice.model.Booking;
import com.apb.bookingservice.model.PaymentOrder;
import com.apb.bookingservice.model.SalonReport;
import com.apb.bookingservice.repository.BookingRepository;
import com.apb.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(BookingRequest booking, UserDTO user, SalonDTO salon, Set<ServiceDTO> services) throws Exception {
        int totalDuration = services.stream()
                .mapToInt(ServiceDTO::getDuration)
                .sum();
        LocalDateTime bookingStartTime = booking.getStartTime();
        LocalDateTime bookingEndTime = bookingStartTime.plusMinutes(totalDuration);

        boolean isSlotAvailable = isTimeSlotAvailable(salon, bookingStartTime, bookingEndTime);
        double totalPrice = services.stream().mapToDouble(ServiceDTO::getPrice).sum();

        Set<Long> idList = services.stream().map(ServiceDTO::getId).collect(Collectors.toSet());

        Booking newBooking = new Booking();
        newBooking.setCustomerId(user.getId());
        newBooking.setSalonId(salon.getId());
        newBooking.setServiceIds(idList);
        newBooking.setStatus(BookingStatus.PENDING);
        newBooking.setTotalPrice(totalPrice);
        newBooking.setStartTime(bookingStartTime);
        newBooking.setEndTime(bookingEndTime);

        return bookingRepository.save(newBooking);
    }

    public boolean isTimeSlotAvailable(SalonDTO salon, LocalDateTime bookingStartTime, LocalDateTime bookingEndTime) throws Exception {

        List<Booking> existingBookings = getBookingsBySalon(salon.getId());

        LocalDateTime salonOpening = salon.getOpeningTime().atDate(bookingStartTime.toLocalDate());
        LocalDateTime salonClosing = salon.getClosingTime().atDate(bookingEndTime.toLocalDate());

        if(bookingStartTime.isBefore(salonOpening) || bookingEndTime.isAfter(salonClosing)) {
            throw new Exception("Booking time must be between salon's working hours");
        }

        for(Booking booking : existingBookings) {
            LocalDateTime existingBookingStartTime = booking.getStartTime();
            LocalDateTime existingBookingEndTime = booking.getEndTime();

            if(bookingStartTime.isBefore(existingBookingEndTime) && bookingEndTime.isAfter(existingBookingStartTime)) {
                throw new Exception("Slot is not available, choose a different time slot");
            }

            if(bookingStartTime.isEqual(existingBookingStartTime) || bookingEndTime.isEqual(existingBookingEndTime)) {
                throw new Exception("Slot is not available, choose a different time slot");
            }

        }

        return true;
    }

    @Override
    public List<Booking> getBookingsByCustomer(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Booking> getBookingsBySalon(Long salonId) {
        return bookingRepository.findBySalonId(salonId);
    }

    @Override
    public Booking getBookingById(Long id) throws Exception {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            throw new Exception("Booking with ID: "+ id +" not found");
        }
        return booking;
    }

    @Override
    public Booking updateBooking(Long bookingId, BookingStatus status) throws Exception {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByDate(LocalDate date, Long salonId) {
        List<Booking> allBookings = getBookingsBySalon(salonId);
        if(date==null){
            return allBookings;
        }
        return allBookings.stream()
                .filter(booking -> isSameDate(booking.getStartTime(), date) || isSameDate(booking.getEndTime(), date))
                .toList();
    }

    private boolean isSameDate(LocalDateTime startTime, LocalDate date) {
        return startTime.toLocalDate().equals(date);
    }

    @Override
    public SalonReport getSalonReport(Long salonId) {
        List<Booking> bookings = getBookingsBySalon(salonId);
        Double totalEarnings = bookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();
        Integer totalBookings = bookings.size();
        List<Booking> cancelledBookings = bookings.stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.CANCELLED))
                .toList();

        Double totalRefund = cancelledBookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        SalonReport report = new SalonReport();
        report.setSalonId(salonId);
        report.setTotalEarnings(totalEarnings);
        report.setTotalBookings(totalBookings);
        report.setTotalRefund(totalRefund);
        report.setCancelledBookings(cancelledBookings.size());

        return report;
    }

    public void deleteBooking(Long bookingId) throws Exception {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            throw new Exception("Booking with ID: "+ bookingId +" not found");
        }
        bookingRepository.delete(booking);
    }

    public void confirmBooking(PaymentOrder order) throws Exception {
        Booking existingBooking = getBookingById(order.getBookingId());
        existingBooking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(existingBooking);
    }

}
