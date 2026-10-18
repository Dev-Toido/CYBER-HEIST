package infrastructure;

// This class represents NEXA's fictional database inside the simulation.
// It is not the actual MySQL connection used to persist application records.
public class Database {
    private boolean databaseProtected;

    public boolean protectDatabase() {
        if (databaseProtected) return false;
        databaseProtected = true;
        return true;
    }

    public boolean accessDatabase() {
        if (databaseProtected) return false;
        return true;
    }

    public void showStatus() {
        System.out.println("\n--- SIMULATED COMPANY DATABASE ---");
        System.out.println("Protection status: " + (databaseProtected ? "PROTECTED" : "AT RISK"));
        System.out.println("Records: fictional demonstration data only");
    }

    public boolean isDatabaseProtected() { return databaseProtected; }
}
