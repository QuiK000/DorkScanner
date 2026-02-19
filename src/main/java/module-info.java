module com.dev.quikkkk.parser {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.dev.quikkkk.parser to javafx.fxml;
    exports com.dev.quikkkk.parser;
}