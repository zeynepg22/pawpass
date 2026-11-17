package pawpass.model;

import java.util.List;

public record TravelDecision(Placement placement, List<DecisionReason> reasons) {
}
