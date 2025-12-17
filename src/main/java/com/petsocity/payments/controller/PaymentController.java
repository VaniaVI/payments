package com.petsocity.payments.controller;

import com.petsocity.payments.dto.PaymentRequest;
import com.petsocity.payments.dto.PaymentResponse;
import com.petsocity.payments.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final MercadoPagoService paymentService;

    // Clave secreta para validar webhook
    @Value("${mercadopago.secret}")
    private String mpSecret;

    // ========================
    // CREAR PAGO (frontend)
    // ========================
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest body) throws Exception {
        PaymentResponse response = paymentService.createPayment(body);
        return ResponseEntity.ok(response);
    }

    // ========================
    // WEBHOOK (Mercado Pago)
    // ========================
    @PostMapping("/webhook")
public ResponseEntity<String> handleWebhook(
        @RequestHeader("x-signature") String xSignature,
        @RequestHeader("x-request-id") String requestId,
        @RequestParam("data.id") String dataId,
        @RequestBody Map<String, Object> webhookData
) {
    try {
        // Validación HMAC (con HmacUtils moderno)
        long ts = Long.parseLong(xSignature.split(",")[0].split("=")[1]);
        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";

        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, mpSecret);
        String hash = hmacUtils.hmacHex(manifest);

        if (!xSignature.contains(hash)) {
            return ResponseEntity.status(403).body("Invalid signature");
        }

        // Procesar la notificación en el service
        paymentService.processWebhook(dataId, webhookData);

        return ResponseEntity.ok("OK");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error processing webhook");
    }
}
}