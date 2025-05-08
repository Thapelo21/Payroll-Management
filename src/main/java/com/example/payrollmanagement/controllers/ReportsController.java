package com.example.payrollmanagement.controllers;

import com.example.payrollmanagement.database.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportsController {

    @FXML private PieChart departmentPieChart;
    @FXML private BarChart<String, Number> salaryBarChart;
    @FXML private LineChart<String, Number> payrollTrendLineChart;
    @FXML private Label totalExpensesLabel;

    @FXML
    private void initialize() {
        departmentPieChart.setTitle("Salary Distribution by Department");
        salaryBarChart.setTitle("Net Salary per Employee");
        payrollTrendLineChart.setTitle("Monthly Payroll Expenses");

        loadPieChartData();
        loadBarChartData();
        loadLineChartData();
        loadTotalExpenses();
    }

    private void loadPieChartData() {
        departmentPieChart.getData().clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT department, SUM(basic_salary + working_hours * 20) AS total_salary FROM employees GROUP BY department")) {
            while (rs.next()) {
                departmentPieChart.getData().add(new PieChart.Data(rs.getString("department"), rs.getDouble("total_salary")));
            }
        } catch (Exception e) {
            System.out.println("Error loading pie chart: " + e.getMessage());
        }
    }

    private void loadBarChartData() {
        salaryBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Net Salary");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name, (basic_salary + working_hours * 20) * 0.85 AS net_salary FROM employees")) {
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("name"), rs.getDouble("net_salary")));
            }
        } catch (Exception e) {
            System.out.println("Error loading bar chart: " + e.getMessage());
        }
        salaryBarChart.getData().add(series);
    }

    private void loadLineChartData() {
        payrollTrendLineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Payroll Expenses Trend");

        // Mock data
        series.getData().add(new XYChart.Data<>("Jan", 12000));
        series.getData().add(new XYChart.Data<>("Feb", 13500));
        series.getData().add(new XYChart.Data<>("Mar", 11000));
        series.getData().add(new XYChart.Data<>("Apr", 14500));

        payrollTrendLineChart.getData().add(series);
    }

    private void loadTotalExpenses() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(basic_salary + working_hours * 20) AS total_expenses FROM employees")) {
            if (rs.next()) {
                totalExpensesLabel.setText("Total Payroll Expenses: $" + String.format("%.2f", rs.getDouble("total_expenses")));
            }
        } catch (Exception e) {
            System.out.println("Error loading total expenses: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/admin_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) departmentPieChart.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            System.out.println("Navigation Error: " + e.getMessage());
        }
    }
}
