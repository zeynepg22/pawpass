package pawpass.checker;

import pawpass.model.Carrier;
import pawpass.model.DecisionReason;
import pawpass.model.Flight;
import pawpass.model.Pet;
import pawpass.model.Placement;
import pawpass.model.Species;
import pawpass.model.TravelDecision;
import pawpass.rules.BreedCategory;
import pawpass.rules.BreedPolicy;
import pawpass.validation.InputValidator;

import java.util.ArrayList;
import java.util.List;

public class TravelEligibilityChecker {

    private static final double MAX_CABIN_WEIGHT_KG = 8.0;
    private static final double MAX_DOG_WEIGHT_IN_HOLD_KG = 14.0;
    private static final double MAX_CAT_WEIGHT_IN_HOLD_KG = 10.0;

    private static final String UNITED_KINGDOM_ISO_CODE = "GB";
    private static final String UNITED_KINGDOM_COMMON_CODE = "UK";

    private final InputValidator validator = new InputValidator();
    private final BreedPolicy breedPolicy = new BreedPolicy();

    public TravelDecision check(Pet pet, Carrier carrier, Flight flight) {
        validator.validate(pet, carrier, flight);

        List<DecisionReason> problems = findProblems(pet, flight);
        if (!problems.isEmpty()) {
            return new TravelDecision(Placement.NOT_ALLOWED, problems);
        }
        return choosePlacement(pet, carrier);
    }

    private List<DecisionReason> findProblems(Pet pet, Flight flight) {
        List<DecisionReason> problems = new ArrayList<>();

        if (isUnitedKingdom(flight.destinationCountryCode())) {
            problems.add(DecisionReason.ROUTE_NOT_ALLOWED);
        }
        if (breedPolicy.categorize(pet.breed()) == BreedCategory.BANNED) {
            problems.add(DecisionReason.BANNED_BREED);
        }
        if (!pet.rabiesVaccinated()) {
            problems.add(DecisionReason.MISSING_RABIES_VACCINE);
        }
        if (!pet.microchipped()) {
            problems.add(DecisionReason.MISSING_MICROCHIP);
        }
        return problems;
    }

    private TravelDecision choosePlacement(Pet pet, Carrier carrier) {
        BreedCategory category = breedPolicy.categorize(pet.breed());
        double totalWeight = pet.weightKg() + carrier.weightKg();

        if (category == BreedCategory.DANGEROUS) {
            return new TravelDecision(Placement.TURKISH_CARGO_ONLY, List.of(DecisionReason.DANGEROUS_BREED));
        }
        if (totalWeight <= MAX_CABIN_WEIGHT_KG) {
            return new TravelDecision(Placement.CABIN, List.of());
        }
        if (category == BreedCategory.SNUB_NOSED) {
            return new TravelDecision(Placement.NOT_ALLOWED, List.of(DecisionReason.SNUB_NOSED_NOT_ALLOWED_IN_HOLD));
        }
        if (isTooHeavyForHold(pet)) {
            return new TravelDecision(Placement.TURKISH_CARGO_ONLY, List.of(DecisionReason.TOO_HEAVY_FOR_HOLD));
        }
        return new TravelDecision(Placement.CARGO_HOLD, List.of());
    }

    private boolean isTooHeavyForHold(Pet pet) {
        if (pet.species() == Species.DOG) {
            return pet.weightKg() > MAX_DOG_WEIGHT_IN_HOLD_KG;
        }
        return pet.weightKg() > MAX_CAT_WEIGHT_IN_HOLD_KG;
    }

    private boolean isUnitedKingdom(String countryCode) {
        return countryCode.equals(UNITED_KINGDOM_ISO_CODE) || countryCode.equals(UNITED_KINGDOM_COMMON_CODE);
    }
}
