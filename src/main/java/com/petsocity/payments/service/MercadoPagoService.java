package com.petsocity.payments.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.petsocity.payments.dto.PaymentRequest;
import com.petsocity.payments.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoService {

    @Value("${mercado.pago.access.token}")
    private String accessToken;

    private final PreferenceClient client = new PreferenceClient();

    public PaymentResponse createPayment(PaymentRequest request) throws Exception {

        // Inicializar access token
        MercadoPagoConfig.setAccessToken(accessToken);

        // Crear items
        List<PreferenceItemRequest> items = new ArrayList<>();
        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id("001")
                .title(request.getSubject())
                .description("Compra en PetSocity")
                .quantity(1)
                .unitPrice(new BigDecimal(request.getAmount()))
                .currencyId("CLP")
                .build();
        items.add(itemRequest);

        // Payer
        PreferencePayerRequest payer = PreferencePayerRequest.builder()
                .email(request.getEmail())
                .build();

        // Back URLs
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
        .success(request.getUrlReturn() + "/success")   // URL de tu frontend o deep link móvil
        .pending(request.getUrlReturn() + "/pending")
        .failure(request.getUrlReturn() + "/failure")
        .build();


        // Crear PreferenceRequest
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .payer(payer)
                .backUrls(backUrls)
                .build();

        

        // Crear preferencia
        Preference preference = client.create(preferenceRequest);

        return new PaymentResponse(preference.getId().toString(), preference.getInitPoint());
    }
        public void processWebhook(String dataId, Map<String, Object> webhookData) {
        // Obtener información del pago desde el webhook
        Map<String, Object> data = (Map<String, Object>) webhookData.get("data");
        Map<String, Object> paymentInfo = data != null ? data : Map.of();

        // Determinar estado real según info de Mercado Pago
        String status = (String) paymentInfo.getOrDefault("status", "pending"); // ejemplo, ajustar según webhook real

        System.out.println("Orden " + dataId + " actualizada con estado: " + status);
    }
}
