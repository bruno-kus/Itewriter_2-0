module com.example.itewriter {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires reactfx;


    opens com.example.itewriter to javafx.fxml;
    exports com.example.itewriter;
}