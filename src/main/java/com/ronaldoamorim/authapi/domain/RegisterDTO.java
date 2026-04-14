package com.ronaldoamorim.authapi.domain;

public record RegisterDTO(String login, String password, UserRole role) {
}