package company;

import java.util.ArrayList;
import java.util.List;

public final class DemoData {
    private DemoData() { }

    public static List<Employee> createEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(101, "Rahul Sharma", "IT", "System Analyst", Employee.AccountStatus.COMPROMISED));
        employees.add(new Employee(102, "Ayesha Patel", "Human Resources", "HR Executive"));
        employees.add(new Employee(103, "Arjun Mehta", "Finance", "Accountant"));
        employees.add(new Employee(104, "Priya Shah", "Engineering", "Software Engineer"));
        employees.add(new Employee(105, "Karan Desai", "Operations", "Operations Analyst"));
        employees.add(new Employee(106, "Nisha Joshi", "IT", "Network Administrator"));
        employees.add(new Employee(107, "Dev Malhotra", "Research", "Research Associate"));
        employees.add(new Employee(108, "Ishita Trivedi", "Customer Support", "Support Lead"));
        return employees;
    }
}
