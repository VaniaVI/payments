package com.petsocity.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String preferenceId;
    private String initPoint; // URL de checkout
}
