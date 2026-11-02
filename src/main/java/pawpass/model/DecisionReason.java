package pawpass.model;

public enum DecisionReason {
    ROUTE_NOT_ALLOWED("Turkish Airlines does not carry pets to this destination"),
    BANNED_BREED("This breed is not accepted on Turkish Airlines flights"),
    DANGEROUS_BREED("This breed can only travel through Turkish Cargo"),
    MISSING_RABIES_VACCINE("A rabies vaccination is required"),
    MISSING_MICROCHIP("Cats and dogs must be microchipped"),
    PREGNANT_ANIMAL("Pregnant animals are not accepted"),
    SEDATED_ANIMAL("Sedated animals are not accepted"),
    SPECIES_NOT_ALLOWED_IN_CABIN("This species can only travel in the cargo hold"),
    SNUB_NOSED_NOT_ALLOWED_IN_HOLD("Snub-nosed breeds cannot travel in the cargo hold"),
    HOLD_REQUIRES_HARD_CASE("The cargo hold requires a hard-case carrier"),
    EXCEEDS_HOLD_LIMITS("Weight or carrier size exceeds cargo hold limits"),
    WATER_DISPENSER_REQUIRED("A water dispenser is required for long flights or layovers");

    private final String message;

    DecisionReason(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
