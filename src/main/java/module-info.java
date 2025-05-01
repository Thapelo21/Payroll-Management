module com.example.payrollmanagement {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.payrollmanagement to javafx.fxml;
    exports com.example.payrollmanagement;
}