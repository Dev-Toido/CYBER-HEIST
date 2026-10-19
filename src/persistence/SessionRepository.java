package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SessionRepository {
    private final Connection connection;

    public SessionRepository(Connection connection) { this.connection = connection; }

    public long start(int targetEmployeeId) throws SQLException {
        String sql = "INSERT INTO simulation_sessions (target_employee_id) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            if (targetEmployeeId > 0) statement.setInt(1, targetEmployeeId);
            else statement.setNull(1, Types.INTEGER);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("MySQL did not return a session ID.");
    }

    public void finish(long sessionId, int exposure, int score, String attackerStatus, String outcome) throws SQLException {
        String sql = "UPDATE simulation_sessions SET ended_at = CURRENT_TIMESTAMP, final_exposure = ?, final_score = ?, attacker_status = ?, outcome = ? WHERE session_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, exposure);
            statement.setInt(2, score);
            statement.setString(3, attackerStatus);
            statement.setString(4, outcome);
            statement.setLong(5, sessionId);
            statement.executeUpdate();
        }
    }

    public List<String> recent(int limit) throws SQLException {
        String sql = "SELECT session_id, started_at, ended_at, final_exposure, final_score, attacker_status, outcome " +
                "FROM simulation_sessions ORDER BY session_id DESC LIMIT ?";
        List<String> rows = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Math.max(1, Math.min(100, limit)));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(String.format("Session #%d | started %s | ended %s | exposure %s%% | score %s | attacker %s | outcome %s",
                            result.getLong("session_id"), result.getTimestamp("started_at"), result.getTimestamp("ended_at"),
                            valueOrDash(result.getObject("final_exposure")), valueOrDash(result.getObject("final_score")),
                            valueOrDash(result.getString("attacker_status")), valueOrDash(result.getString("outcome"))));
                }
            }
        }
        return rows;
    }

    private static String valueOrDash(Object value) { return value == null ? "-" : value.toString(); }
}
