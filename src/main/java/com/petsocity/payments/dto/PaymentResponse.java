package com.petsocity.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
        private String url;
    private String token;

    public String getPaymentUrl() {
        return url + "?token=" + token;
    }
}
