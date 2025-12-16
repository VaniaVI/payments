package com.petsocity.payments.services;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import com.petsocity.payments.dto.PaymentRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlowService {

    @Value("${flow.api.url}")
    private String flowApiUrl;

    @Value("${flow.api.key}")
    private String apiKey;

    @Value("${flow.secret.key}")
    private String secretKey;

     @Value("${orders.api.url}")
    private String ordersApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createPayment(PaymentRequest request) {

        Map<String, String> params = new HashMap<>();
        params.put("apiKey", apiKey);
        params.put("commerceOrder", request.getCommerceOrder());
        params.put("subject", request.getSubject());
        params.put("amount", request.getAmount().toString());
        params.put("email", request.getEmail());
        params.put("paymentMethod", "9"); // Webpay
        params.put("urlConfirmation", request.getUrlConfirmation());
        params.put("urlReturn", request.getUrlReturn());

        String signature = sign(params);
        params.put("s", signature);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            flowApiUrl + "/payment/create",
            params,
            Map.class
        );

        Map<String, Object> body = response.getBody();

        return body.get("url") + "?token=" + body.get("token");
    }

    private String sign(Map<String, String> params) {
        String data = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        return DigestUtils.md5DigestAsHex((data + secretKey).getBytes());
    }

    public void confirmPayment(String token) {

    Map<String, String> params = new HashMap<>();
    params.put("apiKey", apiKey);
    params.put("token", token);
    params.put("s", sign(params));

    ResponseEntity<Map> response = restTemplate.postForEntity(
        flowApiUrl + "/payment/getStatus",
        params,
        Map.class
    );

    Map<String, Object> body = response.getBody();

    if ("2".equals(body.get("status").toString())) {
        // pago aprobado
        notifyOrders(body.get("commerceOrder").toString());
    }
    }

    private void notifyOrders(String orderCode) {
    restTemplate.postForEntity(
        ordersApiUrl + "/api/v1/orders/" + orderCode + "/paid",
        null,
        Void.class
    );
}


}

