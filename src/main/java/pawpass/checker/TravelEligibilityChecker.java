package pawpass.checker;

import pawpass.model.Carrier;
import pawpass.model.CarrierType;
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
import java.util.Set;

public class TravelEligibilityChecker {

    private static final double MAX_CABIN_TOTAL_WEIGHT_KG = 8.0;
    private static final int MAX_CABIN_HEIGHT_CM = 23;
    private static final int MAX_CABIN_WIDTH_CM = 30;
    private static final int MAX_CABIN_LENGTH_CM = 40;

    private static final double MAX_HOLD_TOTAL_WEIGHT_KG = 50.0;
    private static final int MAX_HOLD_HEIGHT_CM = 75;
    private static final int MAX_HOLD_WIDTH_CM = 75;
    private static final int MAX_HOLD_LENGTH_CM = 125;
    private static final double MAX_HOLD_DOG_WEIGHT_KG = 14.0;
    private static final double MAX_HOLD_CAT_WEIGHT_KG = 10.0;

    private static final double WATER_DISPENSER_FLIGHT_HOURS = 8.0;
    private static final double WATER_DISPENSER_LAYOVER_HOURS = 4.0;

    private static final Set<String> BLOCKED_DESTINATIONS = Set.of("GB");
    private static final Set<Species> CABIN_SPECIES =
            Set.of(Species.CAT, Species.DOG, Species.PARAKEET, Species.CANARY);
    private static final Set<Species> MICROCHIP_REQUIRED_SPECIES = Set.of(Species.CAT, Species.DOG);

    public TravelDecision check(Pet pet, Carrier carrier, Flight flight) {
        InputValidator.validate(pet, carrier, flight);

        List<DecisionReason> rejections = findRejections(pet, flight);
        if (!rejections.isEmpty()) {
            return new TravelDecision(Placement.NOT_ALLOWED, rejections);
        }
        return decidePlacement(pet, carrier, flight);
    }

    private List<DecisionReason> findRejections(Pet pet, Flight flight) {
        List<DecisionReason> reasons = new ArrayList<>();
        if (BLOCKED_DESTINATIONS.contains(flight.destinationCountryCode())) {
            reasons.add(DecisionReason.ROUTE_NOT_ALLOWED);
        }
        if (BreedPolicy.categorize(pet.breed()) == BreedCategory.BANNED) {
            reasons.add(DecisionReason.BANNED_BREED);
        }
        if (!pet.rabiesVaccinated()) {
            reasons.add(DecisionReason.MISSING_RABIES_VACCINE);
        }
        if (MICROCHIP_REQUIRED_SPECIES.contains(pet.species()) && !pet.microchipped()) {
            reasons.add(DecisionReason.MISSING_MICROCHIP);
        }
        if (pet.pregnant()) {
            reasons.add(DecisionReason.PREGNANT_ANIMAL);
        }
        if (pet.sedated()) {
            reasons.add(DecisionReason.SEDATED_ANIMAL);
        }
        return reasons;
    }

    private TravelDecision decidePlacement(Pet pet, Carrier carrier, Flight flight) {
        BreedCategory breedCategory = BreedPolicy.categorize(pet.breed());

        if (breedCategory == BreedCategory.DANGEROUS) {
            return decision(Placement.TURKISH_CARGO_ONLY, DecisionReason.DANGEROUS_BREED);
        }
        if (fitsCabin(pet, carrier)) {
            return new TravelDecision(Placement.CABIN, List.of());
        }
        if (breedCategory == BreedCategory.SNUB_NOSED) {
            return decision(Placement.NOT_ALLOWED, DecisionReason.SNUB_NOSED_NOT_ALLOWED_IN_HOLD);
        }
        if (carrier.type() != CarrierType.HARD_CASE) {
            return decision(Placement.NOT_ALLOWED, DecisionReason.HOLD_REQUIRES_HARD_CASE);
        }
        if (!fitsHold(pet, carrier)) {
            return decision(Placement.TURKISH_CARGO_ONLY, DecisionReason.EXCEEDS_HOLD_LIMITS);
        }
        return holdDecision(pet, flight);
    }

    private TravelDecision holdDecision(Pet pet, Flight flight) {
        List<DecisionReason> reasons = new ArrayList<>();
        if (!CABIN_SPECIES.contains(pet.species())) {
            reasons.add(DecisionReason.SPECIES_NOT_ALLOWED_IN_CABIN);
        }
        if (needsWaterDispenser(flight)) {
            reasons.add(DecisionReason.WATER_DISPENSER_REQUIRED);
        }
        return new TravelDecision(Placement.CARGO_HOLD, reasons);
    }

    private boolean fitsCabin(Pet pet, Carrier carrier) {
        return CABIN_SPECIES.contains(pet.species())
                && carrier.type() == CarrierType.SOFT_CASE
                && totalWeight(pet, carrier) <= MAX_CABIN_TOTAL_WEIGHT_KG
                && carrier.heightCm() <= MAX_CABIN_HEIGHT_CM
                && carrier.widthCm() <= MAX_CABIN_WIDTH_CM
                && carrier.lengthCm() <= MAX_CABIN_LENGTH_CM;
    }

    private boolean fitsHold(Pet pet, Carrier carrier) {
        return totalWeight(pet, carrier) <= MAX_HOLD_TOTAL_WEIGHT_KG
                && carrier.heightCm() <= MAX_HOLD_HEIGHT_CM
                && carrier.widthCm() <= MAX_HOLD_WIDTH_CM
                && carrier.lengthCm() <= MAX_HOLD_LENGTH_CM
                && withinSpeciesHoldLimit(pet);
    }

    private boolean withinSpeciesHoldLimit(Pet pet) {
        return switch (pet.species()) {
            case DOG -> pet.weightKg() <= MAX_HOLD_DOG_WEIGHT_KG;
            case CAT -> pet.weightKg() <= MAX_HOLD_CAT_WEIGHT_KG;
            default -> true;
        };
    }

    private boolean needsWaterDispenser(Flight flight) {
        return flight.durationHours() >= WATER_DISPENSER_FLIGHT_HOURS
                || flight.layoverHours() >= WATER_DISPENSER_LAYOVER_HOURS;
    }

    private double totalWeight(Pet pet, Carrier carrier) {
        return pet.weightKg() + carrier.weightKg();
    }

    private TravelDecision decision(Placement placement, DecisionReason reason) {
        return new TravelDecision(placement, List.of(reason));
    }
}
