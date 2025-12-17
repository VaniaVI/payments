package com.petsocity.payments.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PaymentRequest {
    private String commerceOrder;   
    private String subject;         
    private String email;           
    private Integer amount;         
    private String urlReturn;       
    private String urlConfirmation; 
    private List<Map<String, Object>> items; // opcional para carrito real
}
