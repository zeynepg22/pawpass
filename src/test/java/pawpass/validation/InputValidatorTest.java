package pawpass.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pawpass.model.Carrier;
import pawpass.model.Flight;
import pawpass.model.Pet;
import pawpass.model.Species;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InputValidatorTest {

    private InputValidator validator;
    private Pet validPet;
    private Carrier validCarrier;
    private Flight validFlight;

    @BeforeEach
    public void setUp() {
        validator = new InputValidator();
        validPet = new Pet(Species.CAT, "Tabby", 4.0, true, true);
        validCarrier = new Carrier(2.0);
        validFlight = new Flight("DE");
    }

    @AfterEach
    public void tearDown() {
        validator = null;
        validPet = null;
        validCarrier = null;
        validFlight = null;
    }

    // Normal case: all values are valid, no exception
    @Test
    public void testValidateValidInput() {
        assertDoesNotThrow(() -> validator.validate(validPet, validCarrier, validFlight));
    }

    // Exception case: pet is null
    @Test
    public void testValidateNullPet() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(null, validCarrier, validFlight));
    }

    // Exception case: carrier is null
    @Test
    public void testValidateNullCarrier() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, null, validFlight));
    }

    // Exception case: flight is null
    @Test
    public void testValidateNullFlight() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, validCarrier, null));
    }

    // Exception case: species is missing
    @Test
    public void testValidateNullSpecies() {
        Pet pet = new Pet(null, "Tabby", 4.0, true, true);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(pet, validCarrier, validFlight));
    }

    // Boundary: pet weight exactly 0 is rejected
    @Test
    public void testValidateZeroPetWeight() {
        Pet pet = new Pet(Species.CAT, "Tabby", 0.0, true, true);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(pet, validCarrier, validFlight));
    }

    // Boundary: pet weight just above 0 is accepted
    @Test
    public void testValidateSmallestPetWeight() {
        Pet pet = new Pet(Species.CAT, "Tabby", 0.1, true, true);
        assertDoesNotThrow(() -> validator.validate(pet, validCarrier, validFlight));
    }

    // Negative test: pet weight that is not a number is rejected
    @Test
    public void testValidateNotANumberPetWeight() {
        Pet pet = new Pet(Species.CAT, "Tabby", Double.NaN, true, true);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(pet, validCarrier, validFlight));
    }

    // Negative test: carrier weight that is not a number is rejected
    @Test
    public void testValidateNotANumberCarrierWeight() {
        Carrier carrier = new Carrier(Double.NaN);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, carrier, validFlight));
    }

    // Exception case: country code is missing
    @Test
    public void testValidateNullCountryCode() {
        Flight flight = new Flight(null);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, validCarrier, flight));
    }

    // Boundary: country code with three letters (one more than allowed)
    @Test
    public void testValidateThreeLetterCountryCode() {
        Flight flight = new Flight("DEU");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, validCarrier, flight));
    }

    // Exception case: country code in lower case
    @Test
    public void testValidateLowerCaseCountryCode() {
        Flight flight = new Flight("de");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, validCarrier, flight));
    }
}
