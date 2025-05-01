package com.example.payrollmanagement.controllers;

import com.example.payrollmanagement.database.DBConnection;
import com.example.payrollmanagement.models.Employee;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.awt.Desktop;
import java.io.*;
import java.sql.*;

public class EmployeePayslipController {

    @FXML private Label lblEmployeeName;
    @FXML private Label lblEmployeeID;
    @FXML private Label lblGrossSalary;
    @FXML private Label lblNetSalary;
    @FXML private TextArea txtSalaryBreakdown;

    private int employeeId; // to store employee ID

    private static final double TAX_RATE = 0.10;
    private static final double INSURANCE_RATE = 0.05;
    private static final double OTHER_DEDUCTIONS = 50.0;
    private static final double OVERTIME_RATE = 20.0;

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
        loadPayslip();
    }

    private void loadPayslip() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT name, basic_salary, working_hours FROM employees WHERE employee_id = ?")) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double basicSalary = rs.getDouble("basic_salary");
                int workingHours = rs.getInt("working_hours");

                double overtimePay = workingHours * OVERTIME_RATE;
                double grossSalary = basicSalary + overtimePay;

                double tax = grossSalary * TAX_RATE;
                double insurance = grossSalary * INSURANCE_RATE;
                double deductions = tax + insurance + OTHER_DEDUCTIONS;
                double netSalary = grossSalary - deductions;

                lblEmployeeName.setText(name);
                lblEmployeeID.setText(String.valueOf(employeeId));
                lblGrossSalary.setText(String.format("$%.2f", grossSalary));
                lblNetSalary.setText(String.format("$%.2f", netSalary));

                txtSalaryBreakdown.setText(
                        "Basic Salary: $" + basicSalary + "\n" +
                                "Overtime Pay: $" + overtimePay + "\n" +
                                "Tax Deduction (10%): $" + tax + "\n" +
                                "Insurance Deduction (5%): $" + insurance + "\n" +
                                "Other Deductions: $" + OTHER_DEDUCTIONS + "\n" +
                                "Net Salary: $" + netSalary
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load payslip: " + e.getMessage());
        }
    }

    @FXML
    private void handlePrintPayslip() {
        try {
            String fileName = "employee_payslip_" + employeeId + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("Employee Payslip"));
            document.add(new Paragraph("------------------------------------------"));
            document.add(new Paragraph("Employee ID: " + lblEmployeeID.getText()));
            document.add(new Paragraph("Employee Name: " + lblEmployeeName.getText()));
            document.add(new Paragraph("Gross Salary: " + lblGrossSalary.getText()));
            document.add(new Paragraph("Net Salary: " + lblNetSalary.getText()));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Salary Breakdown:"));
            document.add(new Paragraph(txtSalaryBreakdown.getText()));

            document.close();

            Desktop.getDesktop().open(new File(fileName));

            showAlert("Payslip Exported", "Payslip PDF generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to export payslip: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportPayslipCSV() {
        String filePath = "employee_payslip_" + employeeId + ".csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Employee ID,Employee Name, Gross Salary, Net Salary, Tax Deduction, Insurance Deduction, Other Deductions");

            writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                    employeeId,
                    lblEmployeeName.getText(),
                    lblGrossSalary.getText(),
                    lblNetSalary.getText(),
                    txtSalaryBreakdown.getText().split("\n")[2].split(": ")[1],
                    txtSalaryBreakdown.getText().split("\n")[3].split(": ")[1],
                    txtSalaryBreakdown.getText().split("\n")[4].split(": ")[1]
            );

            showAlert("Export Successful", "Payslip exported to CSV: " + filePath);
            Desktop.getDesktop().open(new File(filePath));
        } catch (Exception e) {
            showAlert("Export Failed", "Error exporting to CSV: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/payrollmanagement/employee_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblEmployeeName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Employee Dashboard");
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to return to dashboard: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
