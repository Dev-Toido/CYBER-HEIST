package threats;

public class Threat {
    private final String type;
    private final String target;
    private final int severity;

    public Threat(String type, String target, int severity) {
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Threat type cannot be empty.");
        if (target == null || target.isBlank()) throw new IllegalArgumentException("Threat target cannot be empty.");
        if (severity < 1 || severity > 100) throw new IllegalArgumentException("Severity must be between 1 and 100.");
        this.type = type.trim();
        this.target = target.trim();
        this.severity = severity;
    }

    public void showThreat() {
        System.out.println("Threat Type : " + type);
        System.out.println("Target      : " + target);
        System.out.println("Severity    : " + severity + "/100 (" + getSeverityLabel() + ")");
    }

    public String getSeverityLabel() {
        if (severity >= 80) return "CRITICAL";
        if (severity >= 60) return "HIGH";
        if (severity >= 30) return "MEDIUM";
        return "LOW";
    }

    public String getTarget() { return target; }
    public int getSeverity() { return severity; }
    public String getType() { return type; }
}
