package pawpass.rules;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BreedPolicyTest {

    private BreedPolicy policy;

    @BeforeEach
    public void setUp() {
        policy = new BreedPolicy();
    }

    @AfterEach
    public void tearDown() {
        policy = null;
    }

    @Test
    public void testCategorizeStandardBreed() {
        BreedCategory expected = BreedCategory.STANDARD;
        BreedCategory actual = policy.categorize("Golden Retriever");
        assertEquals(expected, actual);
    }

    @Test
    public void testCategorizeSnubNosedBreed() {
        BreedCategory expected = BreedCategory.SNUB_NOSED;
        BreedCategory actual = policy.categorize("Pug");
        assertEquals(expected, actual);
    }

    @Test
    public void testCategorizeDangerousBreed() {
        BreedCategory expected = BreedCategory.DANGEROUS;
        BreedCategory actual = policy.categorize("Rottweiler");
        assertEquals(expected, actual);
    }

    @Test
    public void testCategorizeBannedBreed() {
        BreedCategory expected = BreedCategory.BANNED;
        BreedCategory actual = policy.categorize("Pitbull Terrier");
        assertEquals(expected, actual);
    }

    @Test
    public void testCategorizeUpperCaseWithSpaces() {
        BreedCategory expected = BreedCategory.SNUB_NOSED;
        BreedCategory actual = policy.categorize("  PUG  ");
        assertEquals(expected, actual);
    }

    @Test
    public void testCategorizeNull() {
        assertThrows(IllegalArgumentException.class, () -> policy.categorize(null));
    }

    @Test
    public void testCategorizeBlank() {
        assertThrows(IllegalArgumentException.class, () -> policy.categorize("   "));
    }
}
