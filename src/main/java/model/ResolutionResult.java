package model;

public class ResolutionResult {
    private int reputationChange;
    private String message;

    public ResolutionResult(int reputationChange, String message) {
        this.reputationChange = reputationChange;
        this.message = message;
    }

    public int getReputationChange() {
        return reputationChange;
    }

    public String getMessage() {
        return message;
    }
}
