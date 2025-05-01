package com.example.payrollmanagement.models;

import javafx.beans.property.*;

public class Employee {
    private final IntegerProperty employeeId;
    private final StringProperty name;
    private final StringProperty department;
    private final StringProperty position;
    private final DoubleProperty basicSalary;
    private final IntegerProperty workingHours;

    private final DoubleProperty grossSalary = new SimpleDoubleProperty(0.0);
    private final DoubleProperty tax = new SimpleDoubleProperty(0.0);
    private final DoubleProperty insurance = new SimpleDoubleProperty(0.0);
    private final DoubleProperty otherDeductions = new SimpleDoubleProperty(0.0);
    private final DoubleProperty totalDeductions = new SimpleDoubleProperty(0.0);
    private final DoubleProperty netSalary = new SimpleDoubleProperty(0.0);

    public Employee(int employeeId, String name, String department, String position, double basicSalary, int workingHours) {
        this.employeeId = new SimpleIntegerProperty(employeeId);
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
        this.position = new SimpleStringProperty(position);
        this.basicSalary = new SimpleDoubleProperty(basicSalary);
        this.workingHours = new SimpleIntegerProperty(workingHours);
    }

    // Getters
    public int getEmployeeId() { return employeeId.get(); }
    public String getName() { return name.get(); }
    public String getDepartment() { return department.get(); }
    public String getPosition() { return position.get(); }
    public double getBasicSalary() { return basicSalary.get(); }
    public int getWorkingHours() { return workingHours.get(); }
    public double getGrossSalary() { return grossSalary.get(); }
    public double getTax() { return tax.get(); }
    public double getInsurance() { return insurance.get(); }
    public double getOtherDeductions() { return otherDeductions.get(); }
    public double getTotalDeductions() { return totalDeductions.get(); }
    public double getNetSalary() { return netSalary.get(); }

    // Property getters (for TableView binding)
    public IntegerProperty employeeIdProperty() { return employeeId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty departmentProperty() { return department; }
    public StringProperty positionProperty() { return position; }
    public DoubleProperty basicSalaryProperty() { return basicSalary; }
    public IntegerProperty workingHoursProperty() { return workingHours; }
    public DoubleProperty grossSalaryProperty() { return grossSalary; }
    public DoubleProperty taxProperty() { return tax; }
    public DoubleProperty insuranceProperty() { return insurance; }
    public DoubleProperty otherDeductionsProperty() { return otherDeductions; }
    public DoubleProperty totalDeductionsProperty() { return totalDeductions; }
    public DoubleProperty netSalaryProperty() { return netSalary; }

    // Setters
    public void setGrossSalary(double grossSalary) { this.grossSalary.set(grossSalary); }
    public void setTax(double tax) { this.tax.set(tax); }
    public void setInsurance(double insurance) { this.insurance.set(insurance); }
    public void setOtherDeductions(double otherDeductions) { this.otherDeductions.set(otherDeductions); }
    public void setTotalDeductions(double totalDeductions) { this.totalDeductions.set(totalDeductions); }
    public void setNetSalary(double netSalary) { this.netSalary.set(netSalary); }
}
