package io.github.zeynepg22.pawpass.model;

public record Carrier(
        CarrierType type,
        double weightKg,
        int heightCm,
        int widthCm,
        int lengthCm
) {
}
