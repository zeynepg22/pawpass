package pawpass.rules;

import java.util.Locale;
import java.util.Set;

public final class BreedPolicy {

    private static final Set<String> BANNED_BREEDS = Set.of(
            "bull terrier",
            "staffordshire bull terrier",
            "american pitbull terrier",
            "american staffordshire terrier",
            "pitbull terrier",
            "japanese tosa",
            "dogo argentino",
            "fila brasileiro",
            "american bully"
    );

    private static final Set<String> DANGEROUS_BREEDS = Set.of(
            "american bulldog",
            "bandog",
            "tosa inu",
            "caucasian ovcharka",
            "dogue de bordeaux",
            "doberman",
            "presa canario",
            "rottweiler",
            "mastiff",
            "wolfdog",
            "anatolian shepherd",
            "boerboel"
    );

    private static final Set<String> SNUB_NOSED_BREEDS = Set.of(
            "burmese",
            "exotic shorthair",
            "persian",
            "himalayan",
            "scottish fold",
            "british shorthair",
            "affenpinscher",
            "pekingese",
            "pug",
            "shar pei",
            "shih tzu",
            "japanese chin",
            "lhasa apso",
            "tibetan spaniel",
            "chow chow",
            "english toy spaniel",
            "english bulldog",
            "french bulldog",
            "boston terrier",
            "boxer",
            "brussels griffon",
            "bull mastiff",
            "english mastiff"
    );

    private BreedPolicy() {
    }

    public static BreedCategory categorize(String breed) {
        String normalizedBreed = normalize(breed);
        if (BANNED_BREEDS.contains(normalizedBreed)) {
            return BreedCategory.BANNED;
        }
        if (DANGEROUS_BREEDS.contains(normalizedBreed)) {
            return BreedCategory.DANGEROUS;
        }
        if (SNUB_NOSED_BREEDS.contains(normalizedBreed)) {
            return BreedCategory.SNUB_NOSED;
        }
        return BreedCategory.STANDARD;
    }

    private static String normalize(String breed) {
        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException("Breed must not be blank");
        }
        return breed.trim()
                .toLowerCase(Locale.ROOT)
                .replace('-', ' ')
                .replaceAll("\\s+", " ");
    }
}
