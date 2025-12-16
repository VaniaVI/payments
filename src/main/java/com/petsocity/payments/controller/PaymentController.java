package com.petsocity.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petsocity.payments.dto.PaymentRequest;
import com.petsocity.payments.dto.PaymentResponse;
import com.petsocity.payments.services.FlowService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
        private final FlowService flowService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request) {

        String url = flowService.createPayment(request);
        return ResponseEntity.ok(new PaymentResponse(url));
    }
}
