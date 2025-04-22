package music.demo.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import music.demo.util.Util;

import java.io.File;
import java.util.List;

public class MainController {
    private ObservableList<String> trackNames = FXCollections.observableArrayList();
    private List<File> audioFiles;
    private MediaPlayer mediaPlayer;
    private int currentIndex = -1;

    private ListView<String> trackListView;
    private Slider progressSlider;
    private Label trackLabel;

    public void initUI(ListView<String> listView, Slider slider, Label label) {
        this.trackListView = listView;
        this.progressSlider = slider;
        this.trackLabel = label;
        listView.setItems(trackNames);
        listView.setOnMouseClicked(e -> {
            int selected = listView.getSelectionModel().getSelectedIndex();
            if (selected != -1) {
                playTrack(selected);
            }
        });
    }

    public void openDirectory() {
        File folder = Util.chooseDirectory();
        if (folder != null) {
            audioFiles = Util.getAudioFiles(folder);
            trackNames.clear();
            for (File file : audioFiles) {
                trackNames.add(Util.getFileName(file.getAbsolutePath()));
            }
        }
    }

    public void play() {
        if (mediaPlayer == null && currentIndex >= 0) {
            playTrack(currentIndex);
        } else if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    public void next() {
        if (audioFiles != null && currentIndex + 1 < audioFiles.size()) {
            playTrack(++currentIndex);
        }
    }

    public void previous() {
        if (audioFiles != null && currentIndex - 1 >= 0) {
            playTrack(--currentIndex);
        }
    }

    private void playTrack(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        File file = audioFiles.get(index);
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        currentIndex = index;
        trackLabel.setText("Сейчас играет: " + file.getName());
        trackListView.getSelectionModel().select(index);

        mediaPlayer.setOnReady(() -> {
            progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            Platform.runLater(() -> {
                progressSlider.setValue(newTime.toSeconds());
                String currentTime = Util.formatTime(newTime);
                String totalTime = Util.formatTime(mediaPlayer.getTotalDuration());
                trackLabel.setText("Сейчас играет: " + file.getName() + " [" + currentTime + "/" + totalTime + "]");
            });
        });

        mediaPlayer.setOnEndOfMedia(this::next);
        mediaPlayer.play();
    }

    public void seek(double seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }
}
