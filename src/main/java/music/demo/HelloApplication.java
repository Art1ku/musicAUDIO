package music.demo;

import music.demo.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainController controller = new MainController();

        Button openFolderBtn = new Button("Открыть папку");
        Button playBtn = new Button("Play");
        Button pauseBtn = new Button("Pause");
        Button stopBtn = new Button("From start");
        Button nextBtn = new Button("Next");
        Button prevBtn = new Button("Previous");

        ListView<String> trackList = new ListView<>();
        Slider progressSlider = new Slider();
        Label trackInfo = new Label("Нет трека");

        controller.initUI(trackList, progressSlider, trackInfo);

        openFolderBtn.setOnAction(e -> controller.openDirectory());
        playBtn.setOnAction(e -> controller.play());
        pauseBtn.setOnAction(e -> controller.pause());
        stopBtn.setOnAction(e -> controller.stop());
        nextBtn.setOnAction(e -> controller.next());
        prevBtn.setOnAction(e -> controller.previous());

        progressSlider.setOnMouseReleased(e -> controller.seek(progressSlider.getValue()));

        HBox controls = new HBox(5, openFolderBtn, playBtn, pauseBtn, stopBtn, prevBtn, nextBtn);
        VBox bottom = new VBox(5, trackInfo, progressSlider, controls);
        BorderPane root = new BorderPane();
        root.setCenter(trackList);
        root.setBottom(bottom);

        // Устанавливаем темный фон для всего окна
        root.setStyle("-fx-background-color: #333333;");

        // Устанавливаем padding для root (всего окна)
        root.setPadding(new javafx.geometry.Insets(20));

        // Устанавливаем темный фон и белый текст для кнопок
        openFolderBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        playBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        pauseBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        stopBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        nextBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        prevBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        // Устанавливаем темный фон и белый текст для списка треков
        trackList.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        // Устанавливаем темный фон и белый текст для информации о треке
        trackInfo.setStyle("-fx-text-fill: white;");

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Audio Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
