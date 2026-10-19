package metrics;

public class DataExposure {

    private int exposure = 0;

    public void increaseExposure(int amount) {
        if (amount > 0) {
            exposure = Math.min(100, exposure + amount);
        }
    }

    public void reduceExposure(int amount) {
        if (amount > 0) {
            exposure = Math.max(0, exposure - amount);
        }
    }

    public void showExposure() {
        String label;

        if (exposure <= 20) {
            label = "SAFE";
        } else if (exposure <= 40) {
            label = "LOW RISK";
        } else if (exposure <= 70) {
            label = "HIGH RISK";
        } else {
            label = "CRITICAL";
        }

        System.out.println("Data Exposure: " + exposure + "%");
        System.out.println("Risk Level: " + label);
    }

    public int getExposure() {
        return exposure;
    }
}
