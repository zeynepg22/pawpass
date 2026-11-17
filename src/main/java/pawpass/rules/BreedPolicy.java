package pawpass.rules;

import java.util.Set;

public class BreedPolicy {

    private static final Set<String> BANNED_BREEDS = Set.of(
            "pitbull terrier",
            "american bully",
            "dogo argentino",
            "bull terrier"
    );

    private static final Set<String> DANGEROUS_BREEDS = Set.of(
            "rottweiler",
            "doberman",
            "dogue de bordeaux",
            "presa canario"
    );

    private static final Set<String> SNUB_NOSED_BREEDS = Set.of(
            "pug",
            "french bulldog",
            "boxer",
            "persian",
            "british shorthair"
    );

    public BreedCategory categorize(String breed) {
        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException("Breed must not be blank");
        }
        String name = breed.trim().toLowerCase();

        if (BANNED_BREEDS.contains(name)) {
            return BreedCategory.BANNED;
        }
        if (DANGEROUS_BREEDS.contains(name)) {
            return BreedCategory.DANGEROUS;
        }
        if (SNUB_NOSED_BREEDS.contains(name)) {
            return BreedCategory.SNUB_NOSED;
        }
        return BreedCategory.STANDARD;
    }
}
