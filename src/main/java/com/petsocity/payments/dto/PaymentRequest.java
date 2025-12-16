package com.petsocity.payments.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String commerceOrder;
    private Integer amount;
    private String subject;
    private String email;
    private String urlConfirmation;
    private String urlReturn;
}
