import Metrics.DataExposure;
import Metrics.SecurityScore;

public class ModuleTest {
    public static void main(String[] args) {

        DataExposure exposure = new DataExposure();
        SecurityScore score = new SecurityScore();

        System.out.println("=== EXPOSURE TESTS ===");

        exposure.showExposure(); // Expected: 0%, SAFE

        exposure.increaseExposure(30);
        exposure.showExposure(); // Expected: 30%, LOW RISK

        exposure.increaseExposure(50);
        exposure.showExposure(); // Expected: 80%, CRITICAL

        exposure.increaseExposure(50);
        exposure.showExposure(); // Expected: 100%, CRITICAL

        exposure.reduceExposure(40);
        exposure.showExposure(); // Expected: 60%, HIGH RISK

        exposure.reduceExposure(100);
        exposure.showExposure(); // Expected: 0%, SAFE

        System.out.println("\n=== SCORE TESTS ===");

        score.investigate();
        score.protectData();
        score.stopAttack();
        score.ignoreThreat();

        score.showScore(); // Expected: 65
    }
}