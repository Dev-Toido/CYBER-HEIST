package threats;

public class Attacker {
    public enum Status { ACTIVE, CONTAINED, EXFILTRATED }

    private static final String[] PATH = {
            "Employee PC", "Web Server", "Internal Network", "Database", "Company Data"
    };
    private final int targetEmployeeId;
    private final String targetEmployeeName;
    private int pathIndex = 0;
    private Status status = Status.ACTIVE;

    public Attacker() { this(-1, "Unknown employee"); }

    public Attacker(int targetEmployeeId, String targetEmployeeName) {
        this.targetEmployeeId = targetEmployeeId;
        this.targetEmployeeName = targetEmployeeName == null ? "Unknown employee" : targetEmployeeName;
    }

    public boolean moveForward() {
        if (status != Status.ACTIVE) return false;
        if (pathIndex >= PATH.length - 1) {
            status = Status.EXFILTRATED;
            return false;
        }
        pathIndex++;
        if (pathIndex == PATH.length - 1) status = Status.EXFILTRATED;
        showStatus();
        return true;
    }

    public void stopAttacker() {
        if (status == Status.ACTIVE) status = Status.CONTAINED;
    }

    public void showStatus() {
        int progress = getProgress();
        System.out.println("\n+---------------- SIMULATED ATTACK STATUS ----------------+");
        System.out.println("| Target employee : " + targetEmployeeName + " (ID " + targetEmployeeId + ")");
        System.out.println("| Current location: " + getCurrentLocation());
        System.out.println("| Next target     : " + getNextTarget());
        System.out.println("| Progress        : " + progress + "%");
        System.out.println("| Status          : " + getStatusLabel());
        System.out.println("+---------------------------------------------------------+");
    }

    public boolean isActive() { return status == Status.ACTIVE; }
    public boolean isContained() { return status == Status.CONTAINED; }
    public boolean isExfiltrated() { return status == Status.EXFILTRATED; }
    public boolean isAtDatabaseOrBeyond() { return pathIndex >= 3; }
    public int getProgress() { return pathIndex * 25; }
    public String getCurrentLocation() { return PATH[pathIndex]; }
    public String getNextTarget() { return pathIndex + 1 < PATH.length ? PATH[pathIndex + 1] : "None"; }
    public int getTargetEmployeeId() { return targetEmployeeId; }
    public String getTargetEmployeeName() { return targetEmployeeName; }
    public Status getStatus() { return status; }

    public String getStatusLabel() {
        return switch (status) {
            case ACTIVE -> "ACTIVE - SIMULATED MOVEMENT";
            case CONTAINED -> "CONTAINED BY DEFENDER";
            case EXFILTRATED -> "SIMULATED DATA EXFILTRATION";
        };
    }
}
