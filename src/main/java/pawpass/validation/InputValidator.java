package pawpass.validation;

import pawpass.model.Carrier;
import pawpass.model.Flight;
import pawpass.model.Pet;

import java.util.Objects;

public final class InputValidator {

    private InputValidator() {
    }

    public static void validate(Pet pet, Carrier carrier, Flight flight) {
        Objects.requireNonNull(pet, "Pet must not be null");
        Objects.requireNonNull(carrier, "Carrier must not be null");
        Objects.requireNonNull(flight, "Flight must not be null");
        validatePet(pet);
        validateCarrier(carrier);
        validateFlight(flight);
    }

    private static void validatePet(Pet pet) {
        Objects.requireNonNull(pet.species(), "Species must not be null");
        requirePositive(pet.weightKg(), "Pet weight");
        if (pet.ageInWeeks() < 0) {
            throw new IllegalArgumentException("Pet age must not be negative");
        }
    }

    private static void validateCarrier(Carrier carrier) {
        Objects.requireNonNull(carrier.type(), "Carrier type must not be null");
        requirePositive(carrier.weightKg(), "Carrier weight");
        requirePositive(carrier.heightCm(), "Carrier height");
        requirePositive(carrier.widthCm(), "Carrier width");
        requirePositive(carrier.lengthCm(), "Carrier length");
    }

    private static void validateFlight(Flight flight) {
        String countryCode = flight.destinationCountryCode();
        if (countryCode == null || !countryCode.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("Destination must be a two-letter country code");
        }
        requirePositive(flight.durationHours(), "Flight duration");
        if (flight.layoverHours() < 0) {
            throw new IllegalArgumentException("Layover must not be negative");
        }
    }

    private static void requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }
}
