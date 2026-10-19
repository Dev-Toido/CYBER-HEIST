package metrics;

public class DataExposure {
    private int exposure;

    public DataExposure() { this(0); }

    public DataExposure(int initialExposure) {
        if (initialExposure < 0 || initialExposure > 100) throw new IllegalArgumentException("Exposure must be 0-100.");
        exposure = initialExposure;
    }

    public void increaseExposure(int amount) {
        if (amount > 0) exposure = Math.min(100, exposure + amount);
    }

    public void reduceExposure(int amount) {
        if (amount > 0) exposure = Math.max(0, exposure - amount);
    }

    public String getRiskLevel() {
        if (exposure <= 20) return "SAFE";
        if (exposure <= 40) return "LOW RISK";
        if (exposure <= 70) return "HIGH RISK";
        return "CRITICAL";
    }

    public void showExposure() {
        int bars = exposure / 5;
        System.out.println("\nData Exposure: " + exposure + "% [" + "#".repeat(bars) + "-".repeat(20 - bars) + "]");
        System.out.println("Risk Level   : " + getRiskLevel());
    }

    public int getExposure() { return exposure; }
}
