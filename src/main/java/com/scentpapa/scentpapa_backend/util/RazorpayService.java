package com.scentpapa.scentpapa_backend.util;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;
import com.scentpapa.scentpapa_backend.models.*;
import com.scentpapa.scentpapa_backend.repository.OrderRepository;
import com.scentpapa.scentpapa_backend.repository.PaymentRepository;
import com.scentpapa.scentpapa_backend.requests.PaymentVerificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorpayService {

    @Value("${razorpay.key_secret}")
    private String razorpaySecret;

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public void createRazorpayOrder(Order order) {
        try {
            log.info("inside razorpay call");
            JSONObject options = new JSONObject();
            options.put("amount", order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue());
            options.put("currency", "INR");
            options.put("receipt", order.getReferenceNumber());
            options.put("payment_capture", 1);

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);
            log.info("returned from razorpay call with razorpayorderid: {}", Optional.ofNullable(razorpayOrder.get("id")));
            order.setRazorpayOrderId(razorpayOrder.get("id"));
            orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Error creating razorpay order!!");
        }
    }

    public String verifyPayment(PaymentVerificationRequest request) {
        Order order = orderRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("razorpay_order_id", request.getRazorpayOrderId());
            jsonObject.put("razorpay_payment_id", request.getRazorpayPaymentId());
            jsonObject.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValidSignature = Utils.verifyPaymentSignature(jsonObject, razorpaySecret);
            if (!isValidSignature) {
                throw new RuntimeException("Invalid payment signature");
            }
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());

            String method = razorpayPayment.get("method");
            log.info("method fetched from razorpay: {}",  method);
            PaymentMethod paymentMethod = mapToPaymentMethod(method);

            Payment payment = Payment.builder()
                    .order(order)
                    .amount(order.getTotalAmount())
                    .paymentMethod(paymentMethod)
                    .status(PaymentStatus.SUCCESS)
                    .razorpayPaymentId(request.getRazorpayPaymentId())
                    .build();

            paymentRepository.save(payment);

            return "Payment verified and order confirmed!";
        } catch (Exception e) {
            log.error("Signature verification failed! ", e);
            return "Payment verification failed!";
        }
    }

    private PaymentMethod mapToPaymentMethod(String method) {
        if (method == null || method.isEmpty()) {
            return PaymentMethod.DEFAULT;
        }
        return switch (method.toLowerCase()) {
            case "card" -> PaymentMethod.CARD;
            case "upi" -> PaymentMethod.UPI;
            case "wallet" -> PaymentMethod.WALLET;
            default -> PaymentMethod.DEFAULT;
        };
    }

    public void refund(Payment payment, BigDecimal amount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payment_id", payment.getRazorpayPaymentId());
        jsonObject.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue());
        try {
            Refund refund = razorpayClient.payments.refund(jsonObject);
            log.info("Refund initiated successfully for payment id: {}, refund id: {}",
                    payment.getRazorpayPaymentId(), refund.get("id"));
            String refundId = refund.get("id");
            payment.setRazorpayRefundId(refundId);

            Date refundDate = refund.get("created_at");
            if (refundDate != null) {
                payment.setRefundDate(refundDate.toInstant());
            }
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        } catch (RazorpayException e) {
            log.error("Error while initiating refund! {}", e.getMessage());
            throw new RuntimeException("Refund could not be initiated");
        }
    }
}