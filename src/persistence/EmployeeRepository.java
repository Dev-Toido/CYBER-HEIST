package persistence;

import company.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    private final Connection connection;

    public EmployeeRepository(Connection connection) { this.connection = connection; }

    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT employee_id, full_name, department, role_title, account_status FROM employees ORDER BY employee_id";
        List<Employee> employees = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                Employee.AccountStatus status = Employee.AccountStatus.valueOf(results.getString("account_status"));
                employees.add(new Employee(
                        results.getInt("employee_id"), results.getString("full_name"),
                        results.getString("department"), results.getString("role_title"), status));
            }
        }
        return employees;
    }

    public void insert(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (employee_id, full_name, department, role_title, account_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employee.getId());
            statement.setString(2, employee.getName());
            statement.setString(3, employee.getDepartment());
            statement.setString(4, employee.getRole());
            statement.setString(5, employee.getAccountStatus().name());
            statement.executeUpdate();
        }
    }

    public void updateStatus(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET account_status = ? WHERE employee_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getAccountStatus().name());
            statement.setInt(2, employee.getId());
            int updated = statement.executeUpdate();
            if (updated == 0) throw new SQLException("Employee ID " + employee.getId() + " does not exist in MySQL.");
        }
    }
}
