package app;

import company.Company;
import company.DemoData;
import company.Employee;
import infrastructure.Database;
import infrastructure.Network;
import metrics.DataExposure;
import metrics.SecurityScore;
import persistence.MySqlConnection;
import persistence.PersistenceService;
import testing.CyberHeistTestSuite;
import threats.Attacker;
import threats.PhishingThreat;
import threats.Threat;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private record Startup(boolean shouldExit, PersistenceService persistence, List<Employee> employees) { }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            printBanner();
            Startup startup = connectOrChooseOffline(scanner);
            if (startup.shouldExit()) {
                System.out.println("CYBER HEIST closed before the simulation started.");
                return;
            }

            PersistenceService persistence = startup.persistence();
            boolean persistent = persistence != null && persistence.isAvailable();
            Company company = new Company("NEXA Technologies");
            for (Employee employee : startup.employees()) company.addEmployee(employee);

            Employee target = chooseTarget(company);
            if (target == null) {
                int nextId = company.getEmployees().stream().mapToInt(Employee::getId).max().orElse(100) + 1;
                target = new Employee(nextId, "Demo Analyst", "IT", "Security Analyst");
                target.compromiseAccount();
                company.addEmployee(target);
                if (persistence != null) persistence.saveNewEmployee(target);
            } else if (!target.isCompromised()) {
                target.compromiseAccount();
                if (persistence != null) persistence.saveEmployeeStatus(target);
            }

            Threat accountThreat = new Threat("Compromised Account", target.getName(), 85);
            PhishingThreat phishingThreat = new PhishingThreat(target.getName(), 75,
                    "Your account will be blocked. Verify your password using this unfamiliar link.");
            Attacker attacker = new Attacker(target.getId(), target.getName());
            Network network = new Network();
            Database companyDatabase = new Database();
            DataExposure exposure = new DataExposure();
            SecurityScore score = new SecurityScore();

            long sessionId = persistence == null ? 0 : persistence.startSession(target.getId());
            long accountIncidentId = persistence == null ? 0 : persistence.recordIncident(sessionId,
                    accountThreat.getType(), target.getId(), accountThreat.getSeverity(), "OPEN",
                    "Simulated compromised account detected for " + target.getName());
            long phishingIncidentId = persistence == null ? 0 : persistence.recordIncident(sessionId,
                    phishingThreat.getType(), target.getId(), phishingThreat.getSeverity(), "OPEN",
                    "Fictional phishing example shown to analyst");

            System.out.println("\nCompany: NEXA Technologies | Employees loaded: " + company.getEmployeeCount());
            System.out.println("Target account: " + target.getName() + " (ID " + target.getId() + ")");
            System.out.println("MySQL persistence: " + (persistent ? "CONNECTED - records will be saved" : "OFFLINE - session data remains in memory"));
            System.out.println("Safety boundary: all attack movement, alerts and infrastructure states are simulated.\n");

            boolean running = true;
            while (running) {
                printMenu(company, exposure, score, attacker, persistence != null && persistence.isAvailable());
                int choice = readInt(scanner, "Select an option: ", 1, 11);
                switch (choice) {
                    case 1 -> {
                        System.out.println("\n--- ACTIVE SECURITY ALERTS ---");
                        accountThreat.showThreat();
                        System.out.println();
                        phishingThreat.showThreat();
                        exposure.increaseExposure(10);
                        score.investigate();
                        if (persistence != null) {
                            persistence.updateIncidentStatus(accountIncidentId, "INVESTIGATING");
                            persistence.updateIncidentStatus(phishingIncidentId, "INVESTIGATING");
                        }
                        audit(persistence, sessionId, "INVESTIGATE", target.getName(), "Analyst reviewed the active threat alerts.", exposure, score);
                    }
                    case 2 -> {
                        company.showEmployees();
                        int id = readInt(scanner, "Enter employee ID to lock: ", 1, Integer.MAX_VALUE);
                        Employee selected = company.findEmployee(id);
                        if (selected == null) {
                            System.out.println("No employee found with ID " + id + ". No state changed.");
                            break;
                        }
                        boolean wasTargetCompromised = selected.getId() == attacker.getTargetEmployeeId()
                                && selected.isCompromised() && attacker.isActive();
                        if (!selected.lockAccount()) {
                            System.out.println(selected.getName() + "'s account is already locked.");
                            break;
                        }
                        System.out.println("Account locked: " + selected.getName() + " (" + selected.getId() + ").");
                        if (persistence != null) persistence.saveEmployeeStatus(selected);
                        if (wasTargetCompromised) {
                            attacker.stopAttacker();
                            exposure.reduceExposure(5);
                            score.stopAttack();
                            if (persistence != null) {
                                persistence.updateIncidentStatus(accountIncidentId, "CONTAINED");
                                persistence.updateIncidentStatus(phishingIncidentId, "CONTAINED");
                            }
                            System.out.println("The targeted compromised account was locked; simulated attacker contained.");
                        } else {
                            System.out.println("This account was secured, but it was not the active attack target; attacker state is unchanged.");
                        }
                        audit(persistence, sessionId, "LOCK_ACCOUNT", selected.getName(), "Account status changed to LOCKED.", exposure, score);
                    }
                    case 3 -> {
                        boolean newlyBlocked = network.blockTraffic();
                        if (!newlyBlocked) {
                            System.out.println("Suspicious traffic is already blocked.");
                        } else {
                            System.out.println("Firewall rule applied: simulated suspicious traffic blocked.");
                            if (attacker.isActive()) {
                                attacker.stopAttacker();
                                exposure.reduceExposure(10);
                                score.stopAttack();
                                if (persistence != null) {
                                    persistence.updateIncidentStatus(accountIncidentId, "CONTAINED");
                                    persistence.updateIncidentStatus(phishingIncidentId, "CONTAINED");
                                }
                            }
                        }
                        audit(persistence, sessionId, "BLOCK_NETWORK", "NEXA network", "Simulated suspicious network traffic was blocked.", exposure, score);
                    }
                    case 4 -> {
                        boolean newlyProtected = companyDatabase.protectDatabase();
                        if (!newlyProtected) {
                            System.out.println("The simulated company database is already protected.");
                        } else {
                            System.out.println("Simulated company database marked PROTECTED.");
                            exposure.reduceExposure(10);
                            score.protectData();
                            if (attacker.isActive() && attacker.isAtDatabaseOrBeyond()) {
                                attacker.stopAttacker();
                                if (persistence != null) {
                                    persistence.updateIncidentStatus(accountIncidentId, "CONTAINED");
                                    persistence.updateIncidentStatus(phishingIncidentId, "CONTAINED");
                                }
                                System.out.println("The attacker had reached the database area and is now contained by the simulated protection.");
                            }
                        }
                        audit(persistence, sessionId, "PROTECT_DATABASE", "NEXA database", "Simulated company database protection action.", exposure, score);
                    }
                    case 5 -> showDashboard(company, network, companyDatabase, attacker, exposure, score, persistence);
                    case 6 -> {
                        if (attacker.isActive()) {
                            boolean moved = attacker.moveForward();
                            if (moved) {
                                exposure.increaseExposure(20);
                                score.ignoreThreat();
                                String status = attacker.isExfiltrated() ? "EXFILTRATED" : "OPEN";
                                if (persistence != null) {
                                    if (attacker.isExfiltrated()) {
                                        persistence.updateIncidentStatus(accountIncidentId, "EXFILTRATED");
                                        persistence.updateIncidentStatus(phishingIncidentId, "EXFILTRATED");
                                    }
                                }
                                audit(persistence, sessionId, "IGNORE_THREAT", attacker.getCurrentLocation(),
                                        "Analyst ignored the threat; simulated attacker advanced to " + attacker.getCurrentLocation() + " (incident state " + status + ").", exposure, score);
                            }
                        } else if (attacker.isExfiltrated()) {
                            System.out.println("The simulation has already reached the data-exfiltration end state.");
                        } else {
                            System.out.println("The simulated attacker is already contained.");
                        }
                    }
                    case 7 -> company.showCompanyDetails();
                    case 8 -> addEmployee(scanner, company, persistence);
                    case 9 -> showHistory(persistence, sessionId);
                    case 10 -> CyberHeistTestSuite.main(new String[0]);
                    case 11 -> {
                        showFinalReport(company, network, companyDatabase, attacker, exposure, score,
                                persistence, sessionId, accountIncidentId, phishingIncidentId);
                        running = false;
                    }
                    default -> System.out.println("Invalid choice.");
                }
                if (running) pause(scanner);
            }
            if (persistence != null) persistence.close();
        }
    }

    private static Startup connectOrChooseOffline(Scanner scanner) {
        while (true) {
            PersistenceService service = null;
            try {
                service = PersistenceService.connect();
                List<Employee> employees = service.loadEmployees();
                if (employees.isEmpty()) {
                    service.seedEmployees(DemoData.createEmployees());
                    employees = service.loadEmployees();
                }
                System.out.println("[DATABASE] Connected to MySQL and verified required tables.");
                return new Startup(false, service, employees);
            } catch (SQLException exception) {
                if (service != null) service.close();
                System.out.println("\n[DATABASE CONNECTION FAILED]");
                System.out.println("Reason: " + exception.getMessage());
                System.out.println("Config file: " + MySqlConnection.configPath());
                if (!MySqlConnection.configFileExists()) {
                    System.out.println("Setup hint: copy config/database.properties.example to config/database.properties and edit it.");
                }
                System.out.println("[R] Retry after fixing MySQL/config | [C] Continue offline | [E] Exit");
                String choice = readLine(scanner, "Choose R/C/E: ").trim().toUpperCase();
                if (choice.equals("C")) {
                    System.out.println("[DATABASE] Continuing in offline mode. Nothing will be saved to MySQL.");
                    return new Startup(false, null, DemoData.createEmployees());
                }
                if (choice.equals("E")) return new Startup(true, null, List.of());
                System.out.println("Retrying connection and re-reading configuration...\n");
            }
        }
    }

    private static Employee chooseTarget(Company company) {
        for (Employee employee : company.getEmployees()) {
            if (employee.isCompromised() && !employee.isAccountLocked()) return employee;
        }
        for (Employee employee : company.getEmployees()) {
            if (!employee.isAccountLocked()) return employee;
        }
        return null;
    }

    private static void printBanner() {
        System.out.println("+==============================================================+");
        System.out.println("|                 THREAT HUNTERS PRESENT                       |");
        System.out.println("|                    C Y B E R  H E I S T                      |");
        System.out.println("|       Java OOP Cybersecurity Incident Simulation              |");
        System.out.println("+==============================================================+");
    }

    private static void printMenu(Company company, DataExposure exposure, SecurityScore score,
                                  Attacker attacker, boolean databaseConnected) {
        System.out.println("\n+------------------------ CONTROL PANEL -----------------------+");
        System.out.printf("| Company: %-24s Employees: %-3d DB: %-7s |%n", company.getName(), company.getEmployeeCount(), databaseConnected ? "ONLINE" : "OFFLINE");
        System.out.printf("| Exposure: %3d%%  Score: %3d/100  Attacker: %-18s |%n",
                exposure.getExposure(), score.getScore(), attacker.getStatus().name());
        System.out.println("+--------------------------------------------------------------+");
        System.out.println("|  1. Investigate threat alerts                                 |");
        System.out.println("|  2. Lock an employee account                                  |");
        System.out.println("|  3. Block suspicious network traffic                          |");
        System.out.println("|  4. Protect the simulated company database                    |");
        System.out.println("|  5. View security dashboard                                   |");
        System.out.println("|  6. Ignore threat (attacker advances)                         |");
        System.out.println("|  7. View employee directory                                   |");
        System.out.println("|  8. Add an employee                                           |");
        System.out.println("|  9. View saved incident/session history                       |");
        System.out.println("| 10. Run automated test subsystem                              |");
        System.out.println("| 11. Final report and exit                                     |");
        System.out.println("+--------------------------------------------------------------+");
    }

    private static void showDashboard(Company company, Network network, Database database,
                                      Attacker attacker, DataExposure exposure, SecurityScore score,
                                      PersistenceService persistence) {
        System.out.println("\n======================= SECURITY DASHBOARD =====================");
        System.out.println("Company: " + company.getName() + " | Employees: " + company.getEmployeeCount());
        long compromised = company.getEmployees().stream().filter(Employee::isCompromised).count();
        long locked = company.getEmployees().stream().filter(Employee::isAccountLocked).count();
        System.out.println("Compromised accounts: " + compromised + " | Locked accounts: " + locked);
        network.showStatus();
        database.showStatus();
        attacker.showStatus();
        exposure.showExposure();
        score.showScore();
        System.out.println("Persistent storage: " + (persistence != null && persistence.isAvailable() ? "CONNECTED" : "OFFLINE"));
        System.out.println("===============================================================");
    }

    private static void addEmployee(Scanner scanner, Company company, PersistenceService persistence) {
        System.out.println("\n--- ADD EMPLOYEE ---");
        int id = readInt(scanner, "Employee ID (positive, unique): ", 1, Integer.MAX_VALUE);
        if (company.findEmployee(id) != null) {
            System.out.println("That ID already exists. No employee was added.");
            return;
        }
        String name = readNonEmpty(scanner, "Full name: ");
        String department = readNonEmpty(scanner, "Department: ");
        String role = readNonEmpty(scanner, "Role/title: ");
        Employee employee = new Employee(id, name, department, role);
        if (!company.addEmployee(employee)) {
            System.out.println("Could not add employee because the ID is not unique.");
            return;
        }
        if (persistence != null && persistence.isAvailable()) persistence.saveNewEmployee(employee);
        System.out.println("Employee added to the current company directory. MySQL save was attempted if connected.");
    }

    private static void showHistory(PersistenceService persistence, long sessionId) {
        System.out.println("\n======================= SAVED HISTORY ==========================");
        if (persistence == null || !persistence.isAvailable()) {
            System.out.println("History is unavailable in offline mode; the database has no new records from this run.");
            return;
        }
        System.out.println("Recent simulation sessions:");
        List<String> sessions = persistence.recentSessions(10);
        if (sessions.isEmpty()) System.out.println("  No saved sessions.");
        else sessions.forEach(row -> System.out.println("  " + row));
        System.out.println("\nRecent incidents:");
        List<String> incidents = persistence.recentIncidents(10);
        if (incidents.isEmpty()) System.out.println("  No saved incidents.");
        else incidents.forEach(row -> System.out.println("  " + row));
        if (sessionId > 0) {
            System.out.println("\nActions in current session:");
            List<String> actions = persistence.recentActions(sessionId, 10);
            if (actions.isEmpty()) System.out.println("  No saved actions yet.");
            else actions.forEach(row -> System.out.println("  " + row));
        }
        System.out.println("===============================================================");
    }

    private static void showFinalReport(Company company, Network network, Database database,
                                        Attacker attacker, DataExposure exposure, SecurityScore score,
                                        PersistenceService persistence, long sessionId,
                                        long accountIncidentId, long phishingIncidentId) {
        System.out.println("\n+======================== FINAL REPORT =========================+");
        System.out.println("Company: " + company.getName());
        System.out.println("Employees recorded in current session: " + company.getEmployeeCount());
        network.showStatus();
        database.showStatus();
        attacker.showStatus();
        exposure.showExposure();
        score.showScore();
        String outcome = attacker.isExfiltrated() ? "DATA_EXFILTRATED" : attacker.isContained() ? "CONTAINED" : "UNRESOLVED";
        if (persistence != null && persistence.isAvailable()) {
            persistence.finishSession(sessionId, exposure.getExposure(), score.getScore(), attacker.getStatus().name(), outcome);
            if (persistence.isAvailable() && attacker.isContained()) {
                persistence.updateIncidentStatus(accountIncidentId, "CONTAINED");
                persistence.updateIncidentStatus(phishingIncidentId, "CONTAINED");
            }
            if (persistence.isAvailable()) {
                System.out.println("Persistence: final report saved to MySQL (session " + (sessionId > 0 ? sessionId : "not created") + ").");
            } else {
                System.out.println("Persistence: database connection failed during finalization; report was not confirmed as saved.");
            }
        } else {
            System.out.println("Persistence: OFFLINE. This final report exists only in this run's console output.");
        }
        System.out.println("Outcome: " + outcome);
        System.out.println("Note: the attacker and all company infrastructure are fictional simulations.");
        System.out.println("+===============================================================+");
    }

    private static void audit(PersistenceService persistence, long sessionId, String action,
                              String target, String details, DataExposure exposure, SecurityScore score) {
        if (persistence != null && persistence.isAvailable()) {
            persistence.recordAction(sessionId, action, target, details, exposure.getExposure(), score.getScore());
        }
    }

    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                int parsed = Integer.parseInt(value);
                if (parsed < min || parsed > max) {
                    System.out.println("Enter a value from " + min + " to " + max + ".");
                } else return parsed;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            String value = readLine(scanner, prompt).trim();
            if (!value.isBlank()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static void pause(Scanner scanner) {
        System.out.print("\nPress Enter to return to the control panel...");
        scanner.nextLine();
    }
}