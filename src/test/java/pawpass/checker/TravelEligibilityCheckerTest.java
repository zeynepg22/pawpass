package pawpass.checker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pawpass.model.Carrier;
import pawpass.model.DecisionReason;
import pawpass.model.Flight;
import pawpass.model.Pet;
import pawpass.model.Placement;
import pawpass.model.Species;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TravelEligibilityCheckerTest {

    private TravelEligibilityChecker checker;
    private Carrier carrier;
    private Flight flightToGermany;

    @BeforeEach
    public void setUp() {
        checker = new TravelEligibilityChecker();
        carrier = new Carrier(2.0);
        flightToGermany = new Flight("DE");
    }

    @AfterEach
    public void tearDown() {
        checker = null;
        carrier = null;
        flightToGermany = null;
    }

    private Pet healthyPet(Species species, String breed, double weightKg) {
        return new Pet(species, breed, weightKg, true, true);
    }

    @Test
    public void testCheckCatInCabin() {
        Pet cat = healthyPet(Species.CAT, "Tabby", 4.0);
        Placement expected = Placement.CABIN;
        Placement actual = checker.check(cat, carrier, flightToGermany).placement();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckDogInHold() {
        Pet dog = healthyPet(Species.DOG, "Golden Retriever", 10.0);
        Placement expected = Placement.CARGO_HOLD;
        Placement actual = checker.check(dog, carrier, flightToGermany).placement();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "5.9, CABIN",
            "6.0, CABIN",
            "6.1, CARGO_HOLD"
    })
    public void testCabinWeightBoundary(double petWeightKg, Placement expected) {
        Pet cat = healthyPet(Species.CAT, "Tabby", petWeightKg);
        Placement actual = checker.check(cat, carrier, flightToGermany).placement();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "13.9, CARGO_HOLD",
            "14.0, CARGO_HOLD",
            "14.1, TURKISH_CARGO_ONLY"
    })
    public void testDogHoldWeightBoundary(double dogWeightKg, Placement expected) {
        Pet dog = healthyPet(Species.DOG, "Golden Retriever", dogWeightKg);
        Placement actual = checker.check(dog, carrier, flightToGermany).placement();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckCatAboveHoldLimit() {
        Pet cat = healthyPet(Species.CAT, "Tabby", 10.1);
        Placement expected = Placement.TURKISH_CARGO_ONLY;
        Placement actual = checker.check(cat, carrier, flightToGermany).placement();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "Golden Retriever, 4.0, CABIN",
            "Golden Retriever, 10.0, CARGO_HOLD",
            "Pug, 4.0, CABIN",
            "Pug, 10.0, NOT_ALLOWED",
            "Rottweiler, 4.0, TURKISH_CARGO_ONLY",
            "Rottweiler, 10.0, TURKISH_CARGO_ONLY"
    })
    public void testBreedDecisionTable(String breed, double weightKg, Placement expected) {
        Pet dog = healthyPet(Species.DOG, breed, weightKg);
        Placement actual = checker.check(dog, carrier, flightToGermany).placement();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckBannedBreed() {
        Pet dog = healthyPet(Species.DOG, "Pitbull Terrier", 4.0);
        List<DecisionReason> expected = List.of(DecisionReason.BANNED_BREED);
        List<DecisionReason> actual = checker.check(dog, carrier, flightToGermany).reasons();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckMissingVaccine() {
        Pet cat = new Pet(Species.CAT, "Tabby", 4.0, true, false);
        List<DecisionReason> expected = List.of(DecisionReason.MISSING_RABIES_VACCINE);
        List<DecisionReason> actual = checker.check(cat, carrier, flightToGermany).reasons();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckMissingMicrochip() {
        Pet cat = new Pet(Species.CAT, "Tabby", 4.0, false, true);
        List<DecisionReason> expected = List.of(DecisionReason.MISSING_MICROCHIP);
        List<DecisionReason> actual = checker.check(cat, carrier, flightToGermany).reasons();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckDestinationGB() {
        Pet cat = healthyPet(Species.CAT, "Tabby", 4.0);
        List<DecisionReason> expected = List.of(DecisionReason.ROUTE_NOT_ALLOWED);
        List<DecisionReason> actual = checker.check(cat, carrier, new Flight("GB")).reasons();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckDestinationUK() {
        Pet cat = healthyPet(Species.CAT, "Tabby", 4.0);
        List<DecisionReason> expected = List.of(DecisionReason.ROUTE_NOT_ALLOWED);
        List<DecisionReason> actual = checker.check(cat, carrier, new Flight("UK")).reasons();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckAllProblemsReported() {
        Pet dog = new Pet(Species.DOG, "Pitbull Terrier", 20.0, false, false);
        List<DecisionReason> expected = List.of(
                DecisionReason.ROUTE_NOT_ALLOWED,
                DecisionReason.BANNED_BREED,
                DecisionReason.MISSING_RABIES_VACCINE,
                DecisionReason.MISSING_MICROCHIP
        );
        List<DecisionReason> actual = checker.check(dog, carrier, new Flight("GB")).reasons();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckInvalidInput() {
        Pet cat = healthyPet(Species.CAT, "Tabby", -1.0);
        assertThrows(IllegalArgumentException.class, () -> checker.check(cat, carrier, flightToGermany));
    }
}
