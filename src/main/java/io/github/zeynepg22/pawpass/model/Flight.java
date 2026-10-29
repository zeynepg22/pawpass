package io.github.zeynepg22.pawpass.model;

public record Flight(
        String destinationCountryCode,
        double durationHours,
        double layoverHours
) {
}
