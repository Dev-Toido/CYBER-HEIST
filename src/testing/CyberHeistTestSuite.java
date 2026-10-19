package testing;

import company.Company;
import company.Employee;
import infrastructure.Database;
import infrastructure.Network;
import metrics.DataExposure;
import metrics.SecurityScore;
import threats.Attacker;
import threats.PhishingThreat;

import java.util.ArrayList;
import java.util.List;

/** Self-contained test subsystem; it does not require MySQL to be running. */
public final class CyberHeistTestSuite {
    private static int passed;
    private static int failed;
    private static final List<String> failures = new ArrayList<>();

    private CyberHeistTestSuite() { }

    public static void main(String[] args) {
        passed = 0;
        failed = 0;
        failures.clear();
        System.out.println("\n============================================================");
        System.out.println("          CYBER HEIST - AUTOMATED MODULE TESTS");
        System.out.println("          No MySQL connection required");
        System.out.println("============================================================");

        test("Employee stores identity and department", () -> {
            Employee e = new Employee(201, "Test User", "IT", "Analyst");
            check(e.getId() == 201 && e.getName().equals("Test User") && e.getDepartment().equals("IT"));
        });
        test("Account can be marked compromised", () -> {
            Employee e = new Employee(202, "Compromised", "IT", "Analyst");
            check(e.compromiseAccount() && e.isCompromised());
        });
        test("Locked account cannot be compromised", () -> {
            Employee e = new Employee(203, "Locked", "HR", "Officer");
            e.lockAccount();
            check(!e.compromiseAccount() && e.isAccountLocked());
        });
        test("Company accepts multiple unique employees", () -> {
            Company c = new Company("Test Company");
            check(c.addEmployee(new Employee(301, "One")) && c.addEmployee(new Employee(302, "Two")) && c.getEmployeeCount() == 2);
        });
        test("Company rejects duplicate employee IDs", () -> {
            Company c = new Company("Test Company");
            c.addEmployee(new Employee(303, "One"));
            check(!c.addEmployee(new Employee(303, "Duplicate")) && c.getEmployeeCount() == 1);
        });
        test("Company finds existing employee and returns null for absent ID", () -> {
            Company c = new Company("Test Company");
            Employee e = new Employee(304, "Lookup"); c.addEmployee(e);
            check(c.findEmployee(304) == e && c.findEmployee(999) == null);
        });
        test("PhishingThreat inherits threat details", () -> {
            PhishingThreat t = new PhishingThreat("Demo Employee", 75, "Training example");
            check(t.getType().equals("Phishing Attack") && t.getSeverity() == 75 && t.getFakeEmail().equals("Training example"));
        });
        test("Exposure starts at zero", () -> check(new DataExposure().getExposure() == 0));
        test("Exposure is capped at 100", () -> {
            DataExposure e = new DataExposure(); e.increaseExposure(150); check(e.getExposure() == 100);
        });
        test("Exposure cannot go below zero", () -> {
            DataExposure e = new DataExposure(); e.reduceExposure(25); check(e.getExposure() == 0);
        });
        test("Risk labels cover SAFE, LOW, HIGH and CRITICAL", () -> {
            DataExposure e = new DataExposure(20); check(e.getRiskLevel().equals("SAFE"));
            e.increaseExposure(1); check(e.getRiskLevel().equals("LOW RISK"));
            e = new DataExposure(41); check(e.getRiskLevel().equals("HIGH RISK"));
            e = new DataExposure(71); check(e.getRiskLevel().equals("CRITICAL"));
        });
        test("Security score awards investigation points", () -> {
            SecurityScore s = new SecurityScore(); s.investigate(); check(s.getScore() == 20);
        });
        test("Security score cannot exceed 100", () -> {
            SecurityScore s = new SecurityScore(); for (int i = 0; i < 10; i++) s.stopAttack(); check(s.getScore() == 100);
        });
        test("Security score cannot fall below zero", () -> {
            SecurityScore s = new SecurityScore(); s.ignoreThreat(); check(s.getScore() == 0);
        });
        test("Attacker advances through the simulated path", () -> {
            Attacker a = new Attacker(101, "Demo Employee");
            check(a.moveForward() && a.getCurrentLocation().equals("Web Server"));
            a.moveForward(); a.moveForward();
            check(a.getCurrentLocation().equals("Database") && a.isActive());
        });
        test("Contained attacker cannot move further", () -> {
            Attacker a = new Attacker(); a.stopAttacker();
            check(!a.isActive() && !a.moveForward() && a.getProgress() == 0 && a.isContained());
        });
        test("Final attacker step reports simulated exfiltration", () -> {
            Attacker a = new Attacker(); for (int i = 0; i < 4; i++) a.moveForward();
            check(a.isExfiltrated() && !a.isActive() && a.getProgress() == 100);
        });
        test("Network traffic can be blocked", () -> {
            Network n = new Network(); check(n.blockTraffic() && n.isTrafficBlocked() && !n.blockTraffic());
        });
        test("Simulated database protection changes state", () -> {
            Database d = new Database(); check(!d.isDatabaseProtected() && d.protectDatabase() && d.isDatabaseProtected());
        });

        System.out.println("\n---------------------- TEST SUMMARY ------------------------");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        for (String failure : failures) System.out.println("FAIL: " + failure);
        System.out.println("Result: " + (failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED"));
        System.out.println("------------------------------------------------------------");
        if (failed > 0) System.exit(1);
    }

    private static void test(String name, Runnable runnable) {
        try {
            runnable.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable throwable) {
            failed++;
            failures.add(name + " -> " + throwable.getMessage());
            System.out.println("[FAIL] " + name);
        }
    }

    private static void check(boolean condition) {
        if (!condition) throw new AssertionError("Expected condition was false.");
    }
}
