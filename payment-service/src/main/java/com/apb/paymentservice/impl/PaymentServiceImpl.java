package com.apb.paymentservice.impl;

import com.apb.paymentservice.messaging.BookingEventProducer;
import com.apb.paymentservice.messaging.NotificationEventProducer;
import com.apb.paymentservice.model.PaymentMethod;
import com.apb.paymentservice.model.PaymentOrder;
import com.apb.paymentservice.model.PaymentOrderStatus;
import com.apb.paymentservice.payload.dto.BookingDTO;
import com.apb.paymentservice.payload.dto.UserDTO;
import com.apb.paymentservice.payload.response.PaymentLinkResponse;
import com.apb.paymentservice.repository.PaymentOrderRepository;
import com.apb.paymentservice.service.PaymentService;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentOrderRepository paymentOrderRepository;
    private final BookingEventProducer bookingEventProducer;
    private final NotificationEventProducer notificationEventProducer;


    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Override
    public PaymentLinkResponse createOrder(UserDTO user, BookingDTO booking, PaymentMethod method) throws StripeException {
        Double amount = booking.getTotalPrice();

        PaymentOrder order = new PaymentOrder();
        order.setAmount(amount);
        order.setPaymentMethod(method);
        order.setBookingId(booking.getId());
        order.setSalonId(booking.getSalonId());
        order.setUserId(user.getId());

        PaymentOrder savedOrder = paymentOrderRepository.save(order);
        PaymentLinkResponse response = new PaymentLinkResponse();

        if(method.equals(PaymentMethod.STRIPE)){
            PaymentLink payment = createStripePaymentLink(user, savedOrder.getAmount(), savedOrder.getId());
            String paymentURL = payment.getUrl();
            String paymentUrlId = payment.getId();
            response.setPaymentLinkURL(paymentURL);
            response.setPaymentLinkId(paymentUrlId);
            savedOrder.setPaymentLinkId(paymentUrlId);
            paymentOrderRepository.save(savedOrder);
        }

        return response;
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        PaymentOrder paymentOrder = paymentOrderRepository.findById(id).orElse(null);
        if(paymentOrder == null){
            throw new Exception("Payment Order with ID: "+ id +" not found");
        }

        return paymentOrder;
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) {
        return paymentOrderRepository.findByPaymentLinkId(paymentId);
    }

    @Override
    public PaymentLink createStripePaymentLink(UserDTO user, Double amount, Long orderId) throws StripeException {
        Long centAmount = amount.longValue();
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment-success/"+orderId)
                .setCancelUrl("http://localhost:3000/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("USD")
                                .setUnitAmount(centAmount)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Salon Appointment Booking")
                                        .build()
                                ).build()
                        ).build()
                ).build();

        Session session = Session.create(params);
        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setUrl(session.getUrl());
        return paymentLink;
    }

    @Override
    public boolean proceedPayment(PaymentOrder order, String paymentId, String paymentUrlId) {
        if(order.getStatus().equals(PaymentOrderStatus.PENDING)){
            if(order.getPaymentMethod().equals(PaymentMethod.STRIPE)){
                bookingEventProducer.sendBookingUpdateEvent(order);
                notificationEventProducer.sendNotification(
                        order.getBookingId(),
                        order.getUserId(),
                        order.getSalonId()
                );
                order.setStatus(PaymentOrderStatus.SUCCESS);
                paymentOrderRepository.save(order);
                return true;
            }
            return false;
        }
        return false;
    }


}
