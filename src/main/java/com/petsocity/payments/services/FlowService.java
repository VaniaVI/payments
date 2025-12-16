package com.petsocity.payments.services;

import org.springframework.stereotype.Service;

import com.petsocity.payments.dto.PaymentRequest;

@Service
public class FlowService {
        public String createPayment(PaymentRequest request) {

        // 🔵 Aquí irá la integración REAL con Flow
        // Por ahora simulamos

        return "https://sandbox.flow.cl/pago/" + request.getOrderCode();
    }
}
