package music.demo;

import music.demo.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainController controller = new MainController();

        Button openFolderBtn = new Button("Открыть папку");
        Button playBtn = new Button("Play");
        Button pauseBtn = new Button("Pause");
        Button stopBtn = new Button("Stop");
        Button nextBtn = new Button("Next");
        Button prevBtn = new Button("Previous");
        Button repeatBtn = new Button("🔁");
        Button shuffleBtn = new Button("🔀");

        ListView<String> trackList = new ListView<>();
        Slider progressSlider = new Slider();
        Label trackInfo = new Label("Нет трека");

        controller.initUI(trackList, progressSlider, trackInfo, repeatBtn, shuffleBtn);

        openFolderBtn.setOnAction(e -> controller.openDirectory());
        playBtn.setOnAction(e -> controller.play());
        pauseBtn.setOnAction(e -> controller.pause());
        stopBtn.setOnAction(e -> controller.stop());
        nextBtn.setOnAction(e -> controller.next());
        prevBtn.setOnAction(e -> controller.previous());
        repeatBtn.setOnAction(e -> controller.toggleRepeat());
        shuffleBtn.setOnAction(e -> controller.toggleShuffle());

        HBox controls = new HBox(5, openFolderBtn, playBtn, pauseBtn, stopBtn, prevBtn, nextBtn, repeatBtn, shuffleBtn);
        VBox bottom = new VBox(5, trackInfo, progressSlider, controls);
        BorderPane root = new BorderPane();
        root.setCenter(trackList);
        root.setBottom(bottom);

        root.setStyle("-fx-background-color: #333333;");
        root.setPadding(new javafx.geometry.Insets(20));

        openFolderBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        playBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        pauseBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        stopBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        nextBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        prevBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        repeatBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        shuffleBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");

        trackList.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        trackInfo.setStyle("-fx-text-fill: white;");

        Scene scene = new Scene(root, 800, 500);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE:
                    controller.togglePlayPause();
                    break;
                case LEFT:
                    controller.seekBackward();
                    break;
                case RIGHT:
                    controller.seekForward();
                    break;
            }
        });

        primaryStage.setTitle("Audio Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
