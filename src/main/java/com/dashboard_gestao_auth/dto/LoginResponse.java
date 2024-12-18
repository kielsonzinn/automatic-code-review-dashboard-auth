package com.dashboard_gestao_auth.dto;

public record LoginResponse(String token, Long expiresIn) {}
