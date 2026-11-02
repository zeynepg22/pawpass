package pawpass.model;

import java.util.List;

public record TravelDecision(Placement placement, List<DecisionReason> reasons) {

    public TravelDecision {
        reasons = List.copyOf(reasons);
    }

    public boolean isAllowed() {
        return placement != Placement.NOT_ALLOWED;
    }
}
