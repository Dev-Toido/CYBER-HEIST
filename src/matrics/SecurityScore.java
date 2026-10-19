package metrics;

public class SecurityScore {
    private int score;

    public void investigate() { update(20, "Threat investigated", 20); }
    public void protectData() { update(25, "Database protection", 25); }
    public void stopAttack() { update(30, "Attack contained", 30); }
    public void ignoreThreat() { update(-10, "Threat ignored", -10); }

    private void update(int delta, String action, int announcedDelta) {
        int old = score;
        score = Math.max(0, Math.min(100, score + delta));
        int actual = score - old;
        if (actual == announcedDelta) {
            System.out.printf("%s: %+d points%n", action, announcedDelta);
        } else {
            System.out.printf("%s: %+d points (score constrained to 0-100)%n", action, actual);
        }
    }

    public void showScore() {
        System.out.println("Security Score: " + score + "/100");
        System.out.println("Defender Rating: " + getRating());
    }

    public String getRating() {
        if (score >= 80) return "EXCELLENT";
        if (score >= 60) return "GOOD";
        if (score >= 40) return "FAIR";
        if (score >= 20) return "NEEDS IMPROVEMENT";
        return "CRITICAL - REVIEW REQUIRED";
    }

    public int getScore() { return score; }
}
