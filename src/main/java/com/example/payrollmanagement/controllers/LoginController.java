package com.example.payrollmanagement.controllers;

import com.example.payrollmanagement.database.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.sql.*;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Admin", "Employee");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue(); // Get the selected role

        if (username.isEmpty() || password.isEmpty() || role == null) {
            statusLabel.setText("Please enter username, password, and select a role.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT role FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role); // Verify role alongside username and password
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (role.equalsIgnoreCase("Admin")) {
                    loadScene("/com/example/payrollmanagement/admin_dashboard.fxml", "Admin Dashboard");
                } else {
                    handleEmployeeDashboard(conn, username);
                }
            } else {
                statusLabel.setText("Invalid credentials or role mismatch.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Database error occurred.");
        }
    }

    private void handleEmployeeDashboard(Connection conn, String username) throws Exception {
        String empQuery = "SELECT employee_id FROM employees WHERE name = ?";
        PreparedStatement empStmt = conn.prepareStatement(empQuery);
        empStmt.setString(1, username);
        ResultSet empRs = empStmt.executeQuery();

        if (empRs.next()) {
            int employeeId = empRs.getInt("employee_id");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/employee_dashboard.fxml"));
            Parent root = loader.load();
            EmployeeDashboardController dashboardController = loader.getController();
            dashboardController.setLoggedInEmployeeId(employeeId);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Employee Dashboard");
            stage.show();
        } else {
            statusLabel.setText("Employee record not found for this user.");
        }
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue(); // Get the selected role

        if (username.isEmpty() || password.isEmpty() || role == null) {
            statusLabel.setText("Please fill in all fields and select a role.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                statusLabel.setText("Username already exists.");
            } else {
                String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, role); // Insert the selected role into the database
                insertStmt.executeUpdate();

                statusLabel.setText("Registration successful! You can now log in.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Database error.");
        }
    }
}