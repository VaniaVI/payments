package com.petsocity.payments.service;

import com.petsocity.payments.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlowService {

        @Value("${flow.api.key}")
        private String apiKey;

        @Value("${flow.secret.key}")
        private String secretKey;

        @Value("${flow.api.url}")
        private String flowUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> createPayment(PaymentRequest req) {

        if (req.getAmount() == null ||
            req.getEmail() == null ||
            req.getSubject() == null ||
            req.getCommerceOrder() == null ||
            req.getUrlReturn() == null ||
            req.getUrlConfirmation() == null) {
        
            throw new IllegalArgumentException("PaymentRequest incompleto: " + req);
        }

        System.out.println("API KEY: " + apiKey);
        System.out.println("SECRET KEY: " + secretKey);
        System.out.println("FLOW URL: " + flowUrl);


        try {
            Map<String, String> params = new HashMap<>();

            params.put("apiKey", apiKey);
            params.put("subject", req.getSubject());
            params.put("currency", "CLP");
            params.put("amount", String.valueOf(req.getAmount()));
            params.put("email", req.getEmail());
            params.put("commerceOrder", req.getCommerceOrder());
            params.put("urlConfirmation", req.getUrlConfirmation());
            params.put("urlReturn", req.getUrlReturn());

            // 🔐 FIRMA
            params.put("s", sign(params));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.setAll(params);

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            flowUrl + "payment/create",
                            request,
                            Map.class
                    );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error creando pago en Flow", e);
        }
    }

    // 🔐 Método de firma Flow
    private String sign(Map<String, String> params) {
        String base = params.entrySet().stream()
                .filter(e -> !e.getKey().equals("s"))
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        return sha256(base + secretKey);
    }

    private String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void confirmPayment(String token) {
    try {
        // 1️⃣ Log para debugging
        System.out.println("Pago confirmado por Flow, token: " + token);

        // 2️⃣ Notificar a Orders que el pago fue confirmado
        String ordersApiUrl = "https://orders-petsocity.up.railway.app/api/v1/orders/confirmPayment";

        // Construir el body con el token
        Map<String, String> body = Map.of("token", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(ordersApiUrl, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Notificación a Orders enviada correctamente.");
        } else {
            System.err.println("Error notificando a Orders: " + response.getStatusCode());
        }

    } catch (Exception e) {
        System.err.println("Error en confirmPayment: " + e.getMessage());
        e.printStackTrace();
    }
}

}
