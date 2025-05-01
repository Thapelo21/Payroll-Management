package com.example.payrollmanagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private Button employeeButton;

    @FXML
    private void openEmployeeManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/employee.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) employeeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Employee Management");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openPayrollManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/payroll.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) employeeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Payroll Processing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/reports.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) employeeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Payroll Reports");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) employeeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
