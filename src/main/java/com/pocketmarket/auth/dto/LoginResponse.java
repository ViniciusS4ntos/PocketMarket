package com.pocketmarket.auth.dto;

public record LoginResponse(String token, String email, String name) {}