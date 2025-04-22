module music.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.base;


    opens music.demo to javafx.fxml;
    exports music.demo;
}