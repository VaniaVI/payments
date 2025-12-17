package com.petsocity.payments.controller;

import com.petsocity.payments.dto.PaymentRequest;
import com.petsocity.payments.dto.PaymentResponse;
import com.petsocity.payments.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final MercadoPagoService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest body) throws Exception {
        PaymentResponse response = paymentService.createPayment(body);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> confirmPayment(@RequestBody Map<String, Object> webhookData) {
        // Aquí procesas eventos de Mercado Pago
        System.out.println("Webhook Mercado Pago recibido: " + webhookData);
        return ResponseEntity.ok().build();
    }
}
