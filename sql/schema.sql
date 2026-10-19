SELECT VERSION();
SHOW DATABASES;


CREATE DATABASE IF NOT EXISTS cyber_heist
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE cyber_heist;
SELECT DATABASE();

CREATE USER IF NOT EXISTS 'cyberheist_app'@'localhost'
  IDENTIFIED BY 'ChangeMe_To_A_Strong_Password';
GRANT SELECT, INSERT, UPDATE, DELETE ON cyber_heist.* TO 'cyberheist_app'@'localhost';

USE cyber_heist;

CREATE TABLE IF NOT EXISTS employees (
    employee_id INT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    department VARCHAR(80) NOT NULL,
    role_title VARCHAR(80) NOT NULL,
    account_status ENUM('ACTIVE', 'COMPROMISED', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS simulation_sessions (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    target_employee_id INT NULL,
    final_exposure TINYINT UNSIGNED NULL,
    final_score TINYINT UNSIGNED NULL,
    attacker_status VARCHAR(32) NULL,
    outcome VARCHAR(40) NULL,
    CONSTRAINT fk_session_target_employee FOREIGN KEY (target_employee_id)
      REFERENCES employees(employee_id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS incidents (
    incident_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    threat_type VARCHAR(100) NOT NULL,
    target_employee_id INT NULL,
    severity TINYINT UNSIGNED NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    details VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_incident_severity CHECK (severity BETWEEN 1 AND 100),
    CONSTRAINT fk_incident_session FOREIGN KEY (session_id)
      REFERENCES simulation_sessions(session_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_incident_employee FOREIGN KEY (target_employee_id)
      REFERENCES employees(employee_id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_incident_session (session_id),
    INDEX idx_incident_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS incident_actions (
    action_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    action_type VARCHAR(60) NOT NULL,
    target VARCHAR(120) NOT NULL,
    details VARCHAR(500) NOT NULL,
    exposure_after TINYINT UNSIGNED NOT NULL,
    score_after TINYINT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_action_session FOREIGN KEY (session_id)
      REFERENCES simulation_sessions(session_id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_action_session (session_id),
    INDEX idx_action_created (created_at)
) ENGINE=InnoDB;

-- If this sample user already existed with a different password, run:
-- ALTER USER 'cyberheist_app'@'localhost' IDENTIFIED BY 'your-new-password';
-- Then put the same password in config/database.properties.
