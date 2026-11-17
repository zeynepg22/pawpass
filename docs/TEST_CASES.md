# PawPass test cases

Every test follows the same structure: **prefix values** (objects created in `setUp`),
**test case values** (the input of the test), **expected result**, and **postfix values** (`tearDown`).
The actual results are taken from the test runs in `test-run-before-fix.txt` and `test-run-after-fix.txt`.
The default objects are a healthy (microchipped and vaccinated) pet, a 2 kg carrier and a flight to Germany (`DE`).

Test levels: `BreedPolicyTest` and `InputValidatorTest` are unit tests of one class.
`TravelEligibilityCheckerTest` is an integration test, because the checker uses the validator and the breed policy.

In total there are 32 test methods; three of them are data-driven, so JUnit runs 41 test cases.

## BreedPolicyTest (7 tests)

| Test name | Input | Expected | Actual result | Case / branch covered |
| --- | --- | --- | --- | --- |
| testCategorizeStandardBreed | "Golden Retriever" | STANDARD | As expected (pass) | Equivalence class: in no list; every `if` false |
| testCategorizeSnubNosedBreed | "Pug" | SNUB_NOSED | As expected (pass) | Equivalence class: snub-nosed list |
| testCategorizeDangerousBreed | "Rottweiler" | DANGEROUS | As expected (pass) | Equivalence class: dangerous list |
| testCategorizeBannedBreed | "Pitbull Terrier" | BANNED | As expected (pass) | Equivalence class: banned list |
| testCategorizeUpperCaseWithSpaces | "  PUG  " | SNUB_NOSED | As expected (pass) | Same class with different spelling |
| testCategorizeNull | null | IllegalArgumentException | As expected (pass) | Exception case: `breed == null` |
| testCategorizeBlank | "   " | IllegalArgumentException | As expected (pass) | Exception case: only spaces |

## InputValidatorTest (12 tests)

| Test name | Input (changed value) | Expected | Actual result | Case / branch covered |
| --- | --- | --- | --- | --- |
| testValidateValidInput | all values valid | no exception | As expected (pass) | Normal case; every `if` false |
| testValidateNullPet | pet = null | IllegalArgumentException | As expected (pass) | First part of the null check |
| testValidateNullCarrier | carrier = null | IllegalArgumentException | As expected (pass) | Second part of the null check |
| testValidateNullFlight | flight = null | IllegalArgumentException | As expected (pass) | Third part of the null check |
| testValidateNullSpecies | species = null | IllegalArgumentException | As expected (pass) | Missing species |
| testValidateZeroPetWeight | pet weight 0.0 | IllegalArgumentException | As expected (pass) | Boundary: exactly at 0 |
| testValidateSmallestPetWeight | pet weight 0.1 | no exception | As expected (pass) | Boundary: just above 0 |
| testValidateNotANumberPetWeight | pet weight NaN | IllegalArgumentException | First run: no exception (fail); after fix: exception (pass) | Negative test (found Defect 2) |
| testValidateNotANumberCarrierWeight | carrier weight NaN | IllegalArgumentException | First run: no exception (fail); after fix: exception (pass) | Negative test (found Defect 2) |
| testValidateNullCountryCode | code = null | IllegalArgumentException | As expected (pass) | Missing country code |
| testValidateThreeLetterCountryCode | code "DEU" | IllegalArgumentException | As expected (pass) | Boundary: length just above 2 |
| testValidateLowerCaseCountryCode | code "de" | IllegalArgumentException | As expected (pass) | Correct length, wrong letter case |

## TravelEligibilityCheckerTest (13 test methods, 22 test cases)

| Test name | Input | Expected | Actual result | Case / branch covered |
| --- | --- | --- | --- | --- |
| testCheckCatInCabin | cat 4 kg | CABIN | As expected (pass) | Normal case: total weight within 8 kg |
| testCheckDogInHold | Golden Retriever 10 kg | CARGO_HOLD | As expected (pass) | Normal case: last `return` of the method |
| testCabinWeightBoundary (3 rows) | cat 5.9 / 6.0 / 6.1 kg + carrier 2 kg | CABIN / CABIN / CARGO_HOLD | As expected (pass) | Boundary 8 kg: below, at, above |
| testDogHoldWeightBoundary (3 rows) | dog 13.9 / 14.0 / 14.1 kg | CARGO_HOLD / CARGO_HOLD / TURKISH_CARGO_ONLY | As expected (pass) | Boundary 14 kg per dog |
| testCheckCatAboveHoldLimit | cat 10.1 kg | TURKISH_CARGO_ONLY | As expected (pass) | Boundary 10 kg per cat; the cat branch of `isTooHeavyForHold` |
| testBreedDecisionTable (6 rows) | Golden Retriever, Pug, Rottweiler at 4 kg and 10 kg | CABIN, CARGO_HOLD, CABIN, NOT_ALLOWED, TURKISH_CARGO_ONLY, TURKISH_CARGO_ONLY | As expected (pass) | Decision table: breed category x fits the cabin |
| testCheckBannedBreed | Pitbull Terrier 4 kg | BANNED_BREED | As expected (pass) | Banned breed is refused even if it fits the cabin |
| testCheckMissingVaccine | cat without rabies vaccine | MISSING_RABIES_VACCINE | As expected (pass) | Vaccine rule |
| testCheckMissingMicrochip | cat without microchip | MISSING_MICROCHIP | As expected (pass) | Microchip rule |
| testCheckDestinationGB | destination "GB" | ROUTE_NOT_ALLOWED | As expected (pass) | Route rule, ISO code |
| testCheckDestinationUK | destination "UK" | ROUTE_NOT_ALLOWED | First run: CABIN, no reasons (fail); after fix: ROUTE_NOT_ALLOWED (pass) | Same equivalence class (found Defect 1) |
| testCheckAllProblemsReported | banned breed, no chip, no vaccine, to GB | four reasons in order | As expected (pass) | Every `if` in `findProblems` true |
| testCheckInvalidInput | pet weight -1.0 | IllegalArgumentException | As expected (pass) | Validation runs before the rules |
