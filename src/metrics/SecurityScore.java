package Metrics;

public class SecurityScore {

    private int score = 0;

    public void investigate() {
        score += 20;
        System.out.println("Threat investigated: +20 points");
    }

    public void protectData() {
        score += 25;
        System.out.println("Data protected: +25 points");
    }

    public void stopAttack() {
        score += 30;
        System.out.println("Attack stopped: +30 points");
    }

    public void ignoreThreat() {
        score -= 10;
        System.out.println("Threat ignored: -10 points");
    }

    public void showScore() {
        System.out.println("Final Security Score: " + score);
    }

    public int getScore() {
        return score;
    }
}
