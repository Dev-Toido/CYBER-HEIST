package persistence;

import company.Employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PersistenceService implements AutoCloseable {
    @FunctionalInterface private interface SqlWrite { void run() throws SQLException; }

    private Connection connection;
    private final EmployeeRepository employees;
    private final SessionRepository sessions;
    private final IncidentRepository incidents;
    private boolean available = true;
    private boolean warningPrinted;

    private PersistenceService(Connection connection) {
        this.connection = connection;
        employees = new EmployeeRepository(connection);
        sessions = new SessionRepository(connection);
        incidents = new IncidentRepository(connection);
    }

    public static PersistenceService connect() throws SQLException {
        Connection connection = MySqlConnection.open();
        try {
            DatabaseSchema.verify(connection);
            return new PersistenceService(connection);
        } catch (SQLException exception) {
            try { connection.close(); } catch (SQLException ignored) { }
            throw exception;
        }
    }

    public List<Employee> loadEmployees() throws SQLException {
        ensureAvailable();
        return employees.findAll();
    }

    public void seedEmployees(List<Employee> demoEmployees) throws SQLException {
        ensureAvailable();
        for (Employee employee : demoEmployees) employees.insert(employee);
    }

    public void saveNewEmployee(Employee employee) {
        safeWrite("save employee", () -> employees.insert(employee));
    }

    public void saveEmployeeStatus(Employee employee) {
        safeWrite("update account status", () -> employees.updateStatus(employee));
    }

    public long startSession(int targetEmployeeId) {
        if (!available) return 0;
        try { return sessions.start(targetEmployeeId); }
        catch (SQLException exception) { disableAfterFailure("start simulation session", exception); return 0; }
    }

    public long recordIncident(long sessionId, String type, Integer employeeId, int severity,
                               String status, String details) {
        if (!available || sessionId <= 0) return 0;
        try { return incidents.recordIncident(sessionId, type, employeeId, severity, status, details); }
        catch (SQLException exception) { disableAfterFailure("record incident", exception); return 0; }
    }

    public void updateIncidentStatus(long incidentId, String status) {
        safeWrite("update incident status", () -> incidents.updateStatus(incidentId, status));
    }

    public void recordAction(long sessionId, String action, String target, String details, int exposure, int score) {
        if (sessionId <= 0) return;
        safeWrite("record analyst action", () -> incidents.recordAction(sessionId, action, target, details, exposure, score));
    }

    public void finishSession(long sessionId, int exposure, int score, String attackerStatus, String outcome) {
        if (sessionId <= 0) return;
        safeWrite("save final report", () -> sessions.finish(sessionId, exposure, score, attackerStatus, outcome));
    }

    public List<String> recentSessions(int limit) {
        if (!available) return List.of();
        try { return sessions.recent(limit); }
        catch (SQLException exception) { disableAfterFailure("read session history", exception); return List.of(); }
    }

    public List<String> recentIncidents(int limit) {
        if (!available) return List.of();
        try { return incidents.recentIncidents(limit); }
        catch (SQLException exception) { disableAfterFailure("read incident history", exception); return List.of(); }
    }

    public List<String> recentActions(long sessionId, int limit) {
        if (!available || sessionId <= 0) return List.of();
        try { return incidents.recentActions(sessionId, limit); }
        catch (SQLException exception) { disableAfterFailure("read action history", exception); return List.of(); }
    }

    public boolean isAvailable() { return available && connection != null; }

    private void safeWrite(String action, SqlWrite write) {
        if (!available) return;
        try { write.run(); }
        catch (SQLException exception) { disableAfterFailure(action, exception); }
    }

    private void ensureAvailable() throws SQLException {
        if (!isAvailable()) throw new SQLException("Persistence is not available.");
    }

    private void disableAfterFailure(String action, SQLException exception) {
        available = false;
        if (!warningPrinted) {
            warningPrinted = true;
            System.out.println("\n[DATABASE WARNING] Could not " + action + ". Continuing in offline mode.");
            System.out.println("Details: " + exception.getMessage());
            System.out.println("Changes after this point will not be persisted. Restart after fixing the connection to resume saving.");
        }
        try { if (connection != null) connection.close(); } catch (SQLException ignored) { }
        connection = null;
    }

    @Override
    public void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) { }
            connection = null;
        }
        available = false;
    }
}
