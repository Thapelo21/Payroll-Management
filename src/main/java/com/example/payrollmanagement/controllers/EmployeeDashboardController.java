package com.example.payrollmanagement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmployeeDashboardController {

    private int loggedInEmployeeId; // store logged-in employee's ID

    public void setLoggedInEmployeeId(int employeeId) {
        this.loggedInEmployeeId = employeeId;
    }

    @FXML
    private void openPayslip() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/employeePayslip.fxml"));
            Parent root = loader.load();

            // Get controller and pass employee ID
            EmployeePayslipController controller = loader.getController();
            controller.setEmployeeId(loggedInEmployeeId); // ✅ pass the ID here

            Stage stage = (Stage) Stage.getWindows().filtered(window -> window.isShowing()).get(0);
            stage.setScene(new Scene(root));
            stage.setTitle("Employee Payslip");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        loadScene("/com/example/payrollmanagement/login-view.fxml", "Login");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) Stage.getWindows().filtered(window -> window.isShowing()).get(0);
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
