package company;

import java.util.Objects;

public class Employee {
    public enum AccountStatus { ACTIVE, COMPROMISED, LOCKED }

    private final int id;
    private final String name;
    private final String department;
    private final String role;
    private AccountStatus accountStatus;

    public Employee(int id, String name) {
        this(id, name, "Unassigned", "Employee", AccountStatus.ACTIVE);
    }

    public Employee(int id, String name, String department, String role) {
        this(id, name, department, role, AccountStatus.ACTIVE);
    }

    public Employee(int id, String name, String department, String role, AccountStatus accountStatus) {
        if (id <= 0) throw new IllegalArgumentException("Employee ID must be positive.");
        this.id = id;
        this.name = requireText(name, "name");
        this.department = requireText(department, "department");
        this.role = requireText(role, "role");
        this.accountStatus = Objects.requireNonNull(accountStatus, "accountStatus");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " cannot be empty.");
        return value.trim();
    }

    public boolean compromiseAccount() {
        if (accountStatus == AccountStatus.LOCKED) return false;
        accountStatus = AccountStatus.COMPROMISED;
        return true;
    }

    public boolean lockAccount() {
        if (accountStatus == AccountStatus.LOCKED) return false;
        accountStatus = AccountStatus.LOCKED;
        return true;
    }

    public void secureAccount() {
        if (accountStatus != AccountStatus.LOCKED) accountStatus = AccountStatus.ACTIVE;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = Objects.requireNonNull(accountStatus, "accountStatus");
    }

    public void showDetails() {
        System.out.printf("ID: %-5d | Name: %-22s | Department: %-16s | Role: %-18s | Account: %s%n",
                id, name, department, role, accountStatus);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public AccountStatus getAccountStatus() { return accountStatus; }
    public boolean isAccountLocked() { return accountStatus == AccountStatus.LOCKED; }
    public boolean isCompromised() { return accountStatus == AccountStatus.COMPROMISED; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + department + " | " + role + " | " + accountStatus;
    }
}
