package pawpass.validation;

import pawpass.model.Carrier;
import pawpass.model.Flight;
import pawpass.model.Pet;

public class InputValidator {

    public void validate(Pet pet, Carrier carrier, Flight flight) {
        if (pet == null || carrier == null || flight == null) {
            throw new IllegalArgumentException("Pet, carrier and flight are required");
        }
        if (pet.species() == null) {
            throw new IllegalArgumentException("Species is required");
        }
        if (isNotPositive(pet.weightKg())) {
            throw new IllegalArgumentException("Pet weight must be positive");
        }
        if (isNotPositive(carrier.weightKg())) {
            throw new IllegalArgumentException("Carrier weight must be positive");
        }
        String code = flight.destinationCountryCode();
        if (code == null || code.length() != 2 || !code.equals(code.toUpperCase())) {
            throw new IllegalArgumentException("Destination must be a two-letter uppercase country code");
        }
    }

    private boolean isNotPositive(double value) {
        return Double.isNaN(value) || value <= 0;
    }
}
