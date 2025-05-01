module com.example.payrollmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;
    requires com.github.librepdf.openpdf;

    opens com.example.payrollmanagement to javafx.fxml;
    opens com.example.payrollmanagement.controllers to javafx.fxml;
    opens com.example.payrollmanagement.models to javafx.base;

    exports com.example.payrollmanagement;
    exports com.example.payrollmanagement.controllers;
    exports com.example.payrollmanagement.models;
}
