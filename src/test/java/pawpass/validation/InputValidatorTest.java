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

    @Test
    public void testValidateValidInput() {
        assertDoesNotThrow(() -> validator.validate(validPet, validCarrier, validFlight));
    }

    @Test
    public void testValidateNullPet() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(null, validCarrier, validFlight));
    }

    @Test
    public void testValidateNullCarrier() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, null, validFlight));
    }

    @Test
    public void testValidateNullFlight() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, validCarrier, null));
    }

    @Test
    public void testValidateNullSpecies() {
        Pet pet = new Pet(null, "Tabby", 4.0, true, true);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(pet, validCarrier, validFlight));
    }

    @Test
    public void testValidateZeroPetWeight() {
        Pet pet = new Pet(Species.CAT, "Tabby", 0.0, true, true);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(pet, validCarrier, validFlight));
    }

    @Test
    public void testValidateSmallestPetWeight() {
        Pet pet = new Pet(Species.CAT, "Tabby", 0.1, true, true);
        assertDoesNotThrow(() -> validator.validate(pet, validCarrier, validFlight));
    }

    @Test
    public void testValidateNotANumberPetWeight() {
        Pet pet = new Pet(Species.CAT, "Tabby", Double.NaN, true, true);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(pet, validCarrier, validFlight));
    }

    @Test
    public void testValidateNotANumberCarrierWeight() {
        Carrier carrier = new Carrier(Double.NaN);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, carrier, validFlight));
    }

    @Test
    public void testValidateNullCountryCode() {
        Flight flight = new Flight(null);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, validCarrier, flight));
    }

    @Test
    public void testValidateThreeLetterCountryCode() {
        Flight flight = new Flight("DEU");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, validCarrier, flight));
    }

    @Test
    public void testValidateLowerCaseCountryCode() {
        Flight flight = new Flight("de");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(validPet, validCarrier, flight));
    }
}
