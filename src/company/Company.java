package company;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Company {
    private final String companyName;
    private final Map<Integer, Employee> employees = new LinkedHashMap<>();

    public Company(String companyName) {
        if (companyName == null || companyName.isBlank()) throw new IllegalArgumentException("Company name cannot be empty.");
        this.companyName = companyName.trim();
    }

    // Compatibility constructor for the original project structure.
    public Company(String companyName, Employee employee) {
        this(companyName);
        addEmployee(employee);
    }

    public boolean addEmployee(Employee employee) {
        if (employee == null || employees.containsKey(employee.getId())) return false;
        employees.put(employee.getId(), employee);
        return true;
    }

    public void showEmployees() {
        System.out.println("\nEMPLOYEE DIRECTORY - " + companyName);
        System.out.println("-".repeat(112));
        if (employees.isEmpty()) {
            System.out.println("No employee records found.");
        } else {
            employees.values().forEach(Employee::showDetails);
        }
        System.out.println("Total employees: " + employees.size());
    }

    public void showCompanyDetails() {
        System.out.println("\nCompany: " + companyName);
        showEmployees();
    }

    public Employee findEmployee(int id) { return employees.get(id); }
    public Employee getEmployee() { return employees.values().stream().findFirst().orElse(null); }
    public List<Employee> getEmployees() { return new ArrayList<>(employees.values()); }
    public String getName() { return companyName; }
    public int getEmployeeCount() { return employees.size(); }
}
