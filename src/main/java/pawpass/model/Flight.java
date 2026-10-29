package pawpass.model;

public record Flight(
        String destinationCountryCode,
        double durationHours,
        double layoverHours
) {
}
