package com.example.payrollmanagement.controllers;

import com.example.payrollmanagement.database.DBConnection;
import com.example.payrollmanagement.models.Employee;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class PayrollController {

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Number> idColumn;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> positionColumn;
    @FXML private TableColumn<Employee, Number> basicSalaryColumn;
    @FXML private TableColumn<Employee, Number> hoursColumn;
    @FXML private TableColumn<Employee, Number> grossSalaryColumn;
    @FXML private TableColumn<Employee, Number> taxColumn;
    @FXML private TableColumn<Employee, Number> insuranceColumn;
    @FXML private TableColumn<Employee, Number> deductionsColumn;
    @FXML private TableColumn<Employee, Number> netSalaryColumn;

    private final ObservableList<Employee> payrollList = FXCollections.observableArrayList();

    private static final double TAX_RATE = 0.10;
    private static final double INSURANCE_RATE = 0.05;
    private static final double OTHER_DEDUCTIONS = 50.0;
    private static final double OVERTIME_RATE = 20.0;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cell -> cell.getValue().employeeIdProperty());
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        positionColumn.setCellValueFactory(cell -> cell.getValue().positionProperty());
        basicSalaryColumn.setCellValueFactory(cell -> cell.getValue().basicSalaryProperty());
        hoursColumn.setCellValueFactory(cell -> cell.getValue().workingHoursProperty());
        grossSalaryColumn.setCellValueFactory(cell -> cell.getValue().grossSalaryProperty());
        taxColumn.setCellValueFactory(cell -> cell.getValue().taxProperty());
        insuranceColumn.setCellValueFactory(cell -> cell.getValue().insuranceProperty());
        deductionsColumn.setCellValueFactory(cell -> cell.getValue().totalDeductionsProperty());
        netSalaryColumn.setCellValueFactory(cell -> cell.getValue().netSalaryProperty());

        employeeTable.setItems(payrollList);
        loadPayrollData();
    }

    private void loadPayrollData() {
        payrollList.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees")) {
            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary"),
                        rs.getInt("working_hours")
                );
                payrollList.add(emp);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load employee data: " + e.getMessage());
        }
    }

    @FXML
    private void handleCalculateSalary() {
        for (Employee emp : payrollList) {
            double basic = emp.getBasicSalary();
            int hours = emp.getWorkingHours();
            double overtime = hours * OVERTIME_RATE;
            double gross = basic + overtime;

            double tax = gross * TAX_RATE;
            double insurance = gross * INSURANCE_RATE;
            double otherDeductions = OTHER_DEDUCTIONS; // fixed
            double totalDeductions = tax + insurance + otherDeductions;

            double net = gross - totalDeductions;

            emp.setGrossSalary(gross);
            emp.setTax(tax);
            emp.setInsurance(insurance);
            emp.setOtherDeductions(otherDeductions);
            emp.setTotalDeductions(totalDeductions);
            emp.setNetSalary(net);
        }
        employeeTable.refresh();
        showAlert("Calculation Complete", "Salaries have been calculated successfully!");
    }

    @FXML
    private void handleExportPayrollCSV() {
        String filePath = "payroll_data.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Employee ID,Name,Position,Basic Salary,Hours,Gross Salary,Tax,Insurance,Other Deductions,Total Deductions,Net Salary");

            for (Employee emp : payrollList) {
                writer.printf("%d,%s,%s,%.2f,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                        emp.getEmployeeId(),
                        emp.getName(),
                        emp.getPosition(),
                        emp.getBasicSalary(),
                        emp.getWorkingHours(),
                        emp.getGrossSalary(),
                        emp.getTax(),
                        emp.getInsurance(),
                        emp.getOtherDeductions(),
                        emp.getTotalDeductions(),
                        emp.getNetSalary());
            }

            showAlert("Export Successful", "Payroll exported to payroll_data.csv");
            Desktop.getDesktop().open(new File(filePath));

        } catch (Exception e) {
            showAlert("Export Failed", "Error exporting to CSV: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportPayrollPDF() {
        String filePath = "payroll_data.pdf";
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph("Payroll Summary Report\n\n"));

            PdfPTable table = new PdfPTable(11);
            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Position");
            table.addCell("Basic Salary");
            table.addCell("Hours");
            table.addCell("Gross Salary");
            table.addCell("Tax");
            table.addCell("Insurance");
            table.addCell("Other Deduction");
            table.addCell("Total Deductions");
            table.addCell("Net Salary");

            for (Employee emp : payrollList) {
                table.addCell(String.valueOf(emp.getEmployeeId()));
                table.addCell(emp.getName());
                table.addCell(emp.getPosition());
                table.addCell(String.format("%.2f", emp.getBasicSalary()));
                table.addCell(String.valueOf(emp.getWorkingHours()));
                table.addCell(String.format("%.2f", emp.getGrossSalary()));
                table.addCell(String.format("%.2f", emp.getTax()));
                table.addCell(String.format("%.2f", emp.getInsurance()));
                table.addCell(String.format("%.2f", emp.getOtherDeductions()));
                table.addCell(String.format("%.2f", emp.getTotalDeductions()));
                table.addCell(String.format("%.2f", emp.getNetSalary()));
            }

            document.add(table);
            document.close();

            showAlert("Export Successful", "Payroll exported to payroll_data.pdf");
            Desktop.getDesktop().open(new File(filePath));

        } catch (Exception e) {
            showAlert("Export Failed", "Error exporting to PDF: " + e.getMessage());
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
            showAlert("Navigation Error", "Failed to return to dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
