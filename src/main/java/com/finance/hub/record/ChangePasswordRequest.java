package com.finance.hub.record;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
