package com.petsocity.payments.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderCode;
    private Integer amount;
    private String email;
}
