package pawpass.model;

public record Pet(
        Species species,
        String breed,
        double weightKg,
        boolean microchipped,
        boolean rabiesVaccinated
) {
}
