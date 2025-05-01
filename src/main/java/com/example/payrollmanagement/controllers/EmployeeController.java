package com.example.payrollmanagement.controllers;

import com.example.payrollmanagement.database.DBConnection;
import com.example.payrollmanagement.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class EmployeeController {

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Number> idColumn;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> departmentColumn;
    @FXML private TableColumn<Employee, String> positionColumn;
    @FXML private TableColumn<Employee, Number> salaryColumn;
    @FXML private TableColumn<Employee, Number> hoursColumn;

    @FXML private TextField nameField;
    @FXML private TextField departmentField;
    @FXML private TextField positionField;
    @FXML private TextField salaryField;
    @FXML private TextField hoursField;

    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().employeeIdProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        departmentColumn.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        salaryColumn.setCellValueFactory(cellData -> cellData.getValue().basicSalaryProperty());
        hoursColumn.setCellValueFactory(cellData -> cellData.getValue().workingHoursProperty());

        employeeTable.setItems(employeeList);
        loadEmployees();

        // ⚡ NEW: when selecting an employee, populate fields
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateEmployeeFields(newSelection);
            }
        });
    }

    private void loadEmployees() {
        employeeList.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees")) {

            while (rs.next()) {
                employeeList.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary"),
                        rs.getInt("working_hours")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateEmployeeFields(Employee emp) {
        nameField.setText(emp.getName());
        departmentField.setText(emp.getDepartment());
        positionField.setText(emp.getPosition());
        salaryField.setText(String.valueOf(emp.getBasicSalary()));
        hoursField.setText(String.valueOf(emp.getWorkingHours()));
    }

    @FXML
    private void handleAddEmployee() {
        String name = nameField.getText();
        String department = departmentField.getText();
        String position = positionField.getText();

        if (name.isEmpty() || department.isEmpty() || position.isEmpty()
                || salaryField.getText().isEmpty() || hoursField.getText().isEmpty()) {
            showAlert("Input Error", "All fields are required.");
            return;
        }

        try {
            double salary = Double.parseDouble(salaryField.getText());
            int hours = Integer.parseInt(hoursField.getText());

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO employees (name, department, position, basic_salary, working_hours) VALUES (?, ?, ?, ?, ?)")) {
                stmt.setString(1, name);
                stmt.setString(2, department);
                stmt.setString(3, position);
                stmt.setDouble(4, salary);
                stmt.setInt(5, hours);
                stmt.executeUpdate();
            }

            clearFields();
            loadEmployees();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Salary and Working Hours must be numeric.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select an employee to delete.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM employees WHERE employee_id = ?")) {
            stmt.setInt(1, selected.getEmployeeId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        clearFields();
        loadEmployees();
    }

    @FXML
    private void handleEditEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select an employee to edit.");
            return;
        }

        String name = nameField.getText();
        String department = departmentField.getText();
        String position = positionField.getText();

        if (name.isEmpty() || department.isEmpty() || position.isEmpty()
                || salaryField.getText().isEmpty() || hoursField.getText().isEmpty()) {
            showAlert("Input Error", "All fields are required.");
            return;
        }

        try {
            double salary = Double.parseDouble(salaryField.getText());
            int hours = Integer.parseInt(hoursField.getText());

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE employees SET name = ?, department = ?, position = ?, basic_salary = ?, working_hours = ? WHERE employee_id = ?")) {
                stmt.setString(1, name);
                stmt.setString(2, department);
                stmt.setString(3, position);
                stmt.setDouble(4, salary);
                stmt.setInt(5, hours);
                stmt.setInt(6, selected.getEmployeeId());
                stmt.executeUpdate();
            }

            clearFields();
            loadEmployees();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Salary and Working Hours must be numeric.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/admin_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) employeeTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        departmentField.clear();
        positionField.clear();
        salaryField.clear();
        hoursField.clear();
    }
}
