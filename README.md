# PawPass

![CI](https://github.com/zeynepg22/pawpass/actions/workflows/ci.yml/badge.svg)

**Can my pet fly with me?** PawPass is a small Java application that checks whether a cat or a dog can travel
on a Turkish Airlines flight and, if so, where: in the cabin, in the cargo hold, or only through Turkish Cargo.
It always explains its decision.

The code is intentionally simple;
the focus is on systematic testing with JUnit 5.

## Example decisions

| Pet | Carrier | Destination | Decision | Reason |
| --- | --- | --- | --- | --- |
| Cat, 6.0 kg | 2.0 kg | Germany | `CABIN` | Total weight is exactly 8 kg |
| Cat, 6.1 kg | 2.0 kg | Germany | `CARGO_HOLD` | Total weight is above 8 kg |
| Pug, 10 kg | 2.0 kg | Germany | `NOT_ALLOWED` | Snub-nosed breeds cannot travel in the hold |
| Golden Retriever, 14.1 kg | 2.0 kg | Germany | `TURKISH_CARGO_ONLY` | Over the 14 kg limit per dog |
| Rottweiler, 4 kg | 2.0 kg | Germany | `TURKISH_CARGO_ONLY` | Dangerous breed |
| Cat, 4 kg | 2.0 kg | United Kingdom | `NOT_ALLOWED` | Pets are not carried to the UK |

## Rules

The rules follow the pet transportation conditions published by Turkish Airlines (snapshot: October 2026).

| Rule | Limit |
| --- | --- |
| Cabin | Pet + carrier at most 8 kg |
| Cargo hold | At most 14 kg per dog and 10 kg per cat; heavier animals go through Turkish Cargo |
| Breeds | Snub-nosed breeds: cabin only. Dangerous breeds: Turkish Cargo only. Banned breeds: never |
| Documents | Rabies vaccination and microchip are required |
| Route | No pets on flights to the United Kingdom |

## Project structure

```
src/main/java/pawpass
├── model        Pet, Carrier, Flight, Species, Placement, DecisionReason, TravelDecision
├── rules        BreedPolicy, BreedCategory
├── validation   InputValidator
└── checker      TravelEligibilityChecker

src/test/java/pawpass
├── rules        BreedPolicyTest
├── validation   InputValidatorTest
└── checker      TravelEligibilityCheckerTest
```

## Run the tests

Requires Java 21 and Maven.

```
mvn verify
```

The coverage report is written to `target/site/jacoco/index.html`; a copy is kept in `docs/coverage`.

## Testing

| Technique | Where it is applied |
| --- | --- |
| Equivalence partitioning | Breed categories, cats and dogs, the codes `GB` and `UK` |
| Boundary value analysis | 8 kg cabin limit, 14 kg dog limit, 10 kg cat limit, weight above 0, country code length |
| Decision table testing | Breed category x fits the cabin |
| Negative testing | Null values, zero and not-a-number weights, invalid country codes |
| Regression testing | The whole suite is rerun after every change |

Results: 41 tests, 100 % line coverage and 100 % branch coverage (JaCoCo).

The tests follow one style: the object under test is created in `setUp`, every case has its own test method
with explicit `expected` and `actual` values, exception cases are separate tests, and rules with many
input rows use one data-driven test. All test cases are listed in `docs/TEST_CASES.md`.

### Defects found by the tests

| Defect | Found by | Fix |
| --- | --- | --- |
| Destination code `UK` was accepted although flights to the United Kingdom are not allowed (only `GB` was blocked) | Equivalence partitioning | Both codes are now rejected |
| A weight that is not a number (`NaN`) passed validation | Negative testing | Not-a-number values are now rejected |

Test outputs from before and after the fixes are in the `docs` folder.

## Out of scope

To deliver a complete and fully tested project on time, these Turkish Airlines rules are not implemented:
birds, carrier type and carrier dimensions, the 50 kg total limit in the hold, water dispensers on long flights,
pregnant and sedated animals, age limits, two pets in one carrier, destination country rules and fees.
The breed lists contain a representative subset of the airline's lists.

This project is not affiliated with Turkish Airlines. Always check the airline's website before travelling.
