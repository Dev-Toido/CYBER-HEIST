# CYBER HEIST — Cybersecurity Attack Simulation

A Java OOP-based cybersecurity simulation in which the player takes the role of a security analyst defending **NEXA Technologies**, a fictional company, from simulated cyber threats.

> **Project status:** In development. The features described below are the project's planned scope and may change as implementation progresses.

## Overview

NEXA Technologies' fictional network includes employees and employee accounts, a firewall, a web server, a mail server, and a database. During a scenario, the system generates simulated incidents such as phishing, compromised accounts, and suspicious login attempts.

The player investigates alerts and chooses how to respond. Each decision changes the simulated incident state, including attacker progress, data exposure, system security, and the overall security score. At the end of the scenario, the application produces an incident report summarizing the outcome.

## Planned Features

- Simulated cybersecurity incidents, including phishing, account compromise, and suspicious logins
- An investigation flow in which the player reviews incident information before acting
- Defensive actions such as investigating a threat, locking a compromised account, blocking suspicious traffic, securing the database, or ignoring an alert
- State changes based on the player's decisions
- Tracking of simulated attacker progress, data exposure, system security, and security score
- A final incident report showing the scenario outcome and security status

## How It Works

1. A simulated security incident occurs in NEXA Technologies' environment.
2. The player reviews the alert and investigates the available evidence.
3. The player chooses a response, such as locking an account or blocking suspicious traffic.
4. The simulation updates the incident and company security state based on that decision.
5. The scenario ends with an incident report and final security score.

## Java OOP Concepts

The project is intended to demonstrate the following concepts in a practical context:

- **Classes and objects:** Model employees, accounts, servers, threats, actions, and reports.
- **Encapsulation:** Keep each object's data controlled through its methods.
- **Inheritance:** Represent related threat types using a common parent type where appropriate.
- **Polymorphism:** Allow different threat types or defensive actions to produce different behavior through a common interface or parent type.
- **Abstraction:** Expose essential operations while hiding internal implementation details.
- **Constructors and methods:** Initialize simulation entities and implement their behavior.

## Proposed Simulation Components

The initial design may include components such as:

- `Company` — represents NEXA Technologies and its simulated environment
- `Employee` and `EmployeeAccount` — represent staff and account status
- `Threat` — a common model for simulated security threats
- `PhishingAttack`, `AccountCompromise`, and `SuspiciousLogin` — example threat types
- `Firewall`, `WebServer`, `MailServer`, and `Database` — represent protected assets
- `SecurityAction` — represents an analyst's defensive response
- `SimulationEngine` — applies decisions and updates the scenario state
- `IncidentReport` — summarizes the final outcome

These are proposed design elements, not a guarantee of the final class names or architecture.

## Technology

- **Language:** Java
- **Primary focus:** Object-Oriented Programming and simulation logic
- **Interface:** To be finalized by the team (for example, a console interface or a Java desktop GUI)

## Getting Started

The project is currently being planned and developed. Build and run instructions will be added when the source structure and application entry point are established.

## Scope and Safety

CYBER HEIST is an educational simulation. Threats, network components, attacker movement, and defensive actions are represented inside a fictional environment. The application is not intended to attack real systems, scan real networks, collect real credentials, or deploy malware.

## Project Roadmap

- [ ] Finalize scenarios and simulation rules
- [ ] Design the class model and relationships
- [ ] Implement the core simulation engine
- [ ] Implement threat investigation and defensive actions
- [ ] Track security state and calculate the final score
- [ ] Generate the incident report
- [ ] Build the user interface
- [ ] Test scenario outcomes and document how to run the project

## Team — TEAM VYKD

The project is being developed by four members, with responsibilities divided by simulation component.

### 1. Vivek Garai — Threat & Attacker Management

Responsible for simulating cyber threats and managing attacker progression through the fictional company environment.

**Assigned files:**
- `Threat.java` — base model for simulated threats
- `PhishingThreat.java` — phishing incident behavior
- `Attacker.java` — attacker state and simulated movement

### 2. Kashish Vyas — Main System & Integration

Responsible for the main program, coordinating the overall flow, and integrating and testing the team's modules.

**Assigned files:**
- `Main.java` — application entry point and main control flow
- `SecurityScore.java` — tracks and calculates the security score

**Additional responsibilities:** control panel/menu options, final incident report, integration, and testing.

### 3. Yashi Kumari — Company & Employee Management

Responsible for the fictional company's employees and account management.

**Assigned files:**
- `Company.java` — company environment and employee records
- `Employee.java` — employee details and account status

**Additional responsibilities:** employee records and account-locking behavior.

### 4. Shah Devanshi — Network & Database Security

Responsible for the simulated network infrastructure and protection of company data.

**Assigned files:**
- `Network.java` — network components and simulated traffic controls
- `Database.java` — fictional company database and its protection state
- `DataExposure.java` — tracks simulated data exposure

**Additional responsibilities:** firewall behavior, database protection, and data-exposure tracking.

> File assignments represent the current work division and may be adjusted as implementation progresses. All team members will coordinate on integration and testing.

## Academic Purpose

This project is being developed as a Java OOP course project to practice object-oriented design through a simulated cybersecurity incident-response scenario.
