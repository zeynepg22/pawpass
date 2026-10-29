package pawpass.model;

public record Pet(
        Species species,
        String breed,
        double weightKg,
        int ageInWeeks,
        boolean microchipped,
        boolean rabiesVaccinated,
        boolean pregnant,
        boolean sedated
) {
}
