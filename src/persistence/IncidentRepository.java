package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class IncidentRepository {
    private final Connection connection;

    public IncidentRepository(Connection connection) { this.connection = connection; }

    public long recordIncident(long sessionId, String threatType, Integer employeeId, int severity,
                               String status, String details) throws SQLException {
        String sql = "INSERT INTO incidents (session_id, threat_type, target_employee_id, severity, status, details) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, sessionId);
            statement.setString(2, threatType);
            if (employeeId == null || employeeId <= 0) statement.setNull(3, Types.INTEGER);
            else statement.setInt(3, employeeId);
            statement.setInt(4, severity);
            statement.setString(5, status);
            statement.setString(6, details);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("MySQL did not return an incident ID.");
    }

    public void updateStatus(long incidentId, String status) throws SQLException {
        if (incidentId <= 0) return;
        try (PreparedStatement statement = connection.prepareStatement("UPDATE incidents SET status = ? WHERE incident_id = ?")) {
            statement.setString(1, status);
            statement.setLong(2, incidentId);
            statement.executeUpdate();
        }
    }

    public void recordAction(long sessionId, String actionType, String target, String details,
                             int exposure, int score) throws SQLException {
        String sql = "INSERT INTO incident_actions (session_id, action_type, target, details, exposure_after, score_after) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, sessionId);
            statement.setString(2, actionType);
            statement.setString(3, target == null || target.isBlank() ? "N/A" : target);
            statement.setString(4, details);
            statement.setInt(5, exposure);
            statement.setInt(6, score);
            statement.executeUpdate();
        }
    }

    public List<String> recentIncidents(int limit) throws SQLException {
        String sql = "SELECT incident_id, session_id, threat_type, target_employee_id, severity, status, details, created_at " +
                "FROM incidents ORDER BY incident_id DESC LIMIT ?";
        List<String> rows = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Math.max(1, Math.min(100, limit)));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(String.format("Incident #%d | session %d | %s | employee %s | severity %d | %s | %s | %s",
                            result.getLong("incident_id"), result.getLong("session_id"), result.getString("threat_type"),
                            result.getObject("target_employee_id") == null ? "-" : result.getInt("target_employee_id"),
                            result.getInt("severity"), result.getString("status"), result.getString("details"),
                            result.getTimestamp("created_at")));
                }
            }
        }
        return rows;
    }

    public List<String> recentActions(long sessionId, int limit) throws SQLException {
        String sql = "SELECT action_type, target, details, exposure_after, score_after, created_at " +
                "FROM incident_actions WHERE session_id = ? ORDER BY action_id DESC LIMIT ?";
        List<String> rows = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, sessionId);
            statement.setInt(2, Math.max(1, Math.min(100, limit)));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(String.format("%s | target %s | exposure %d%% | score %d | %s | %s",
                            result.getString("action_type"), result.getString("target"), result.getInt("exposure_after"),
                            result.getInt("score_after"), result.getString("details"), result.getTimestamp("created_at")));
                }
            }
        }
        return rows;
    }
}
