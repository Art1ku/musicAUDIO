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
    private boolean isUserSeeking = false;
    private boolean isRepeatEnabled = false;
    private boolean isShuffleEnabled = false;
    private java.util.Random random = new java.util.Random();
    private boolean isSeekingManually = false;


    private ListView<String> trackListView;
    private Slider progressSlider;
    private Label trackLabel;
    private Button repeatBtn;
    private Button shuffleBtn;

    public void initUI(ListView<String> listView, Slider slider, Label label, Button repeatButton, Button shuffleButton) {
        this.trackListView = listView;
        this.progressSlider = slider;
        this.trackLabel = label;
        this.repeatBtn = repeatButton;
        this.shuffleBtn = shuffleButton;

        listView.setItems(trackNames);

        listView.setOnMouseClicked(e -> {
            int selected = listView.getSelectionModel().getSelectedIndex();
            if (selected != -1) {
                playTrack(selected);
            }
        });

        progressSlider.setOnMousePressed(e -> {
            isUserSeeking = true;
        });

        progressSlider.setOnMouseDragged(e -> {
            if (mediaPlayer != null) {
                String currentTime = Util.formatTime(Duration.seconds(progressSlider.getValue()));
                String totalTime = Util.formatTime(mediaPlayer.getTotalDuration());
                trackLabel.setText("Rewind: " + currentTime + " / " + totalTime);
            }
        });

        progressSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
                isUserSeeking = false;
                mediaPlayer.play();
            }
        });
    }

    public void toggleRepeat() {
        isRepeatEnabled = !isRepeatEnabled;
        if (mediaPlayer != null) {
            if (isRepeatEnabled) {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                repeatBtn.setStyle("-fx-background-color: #666666; -fx-text-fill: white;");
            } else {
                mediaPlayer.setCycleCount(1);
                repeatBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
            }
        }
    }

    public void toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled;
        if (isShuffleEnabled) {
            shuffleBtn.setStyle("-fx-background-color: #666666; -fx-text-fill: white;");
        } else {
            shuffleBtn.setStyle("-fx-background-color: #444444; -fx-text-fill: white;");
        }
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                pause();
            } else {
                play();
            }
        }
    }

    public void seekForward() {
        if (mediaPlayer != null) {
            isSeekingManually = true;

            Duration currentTime = mediaPlayer.getCurrentTime();
            Duration totalDuration = mediaPlayer.getTotalDuration();
            double newTime = Math.min(currentTime.toSeconds() + 5, totalDuration.toSeconds());

            mediaPlayer.seek(Duration.seconds(newTime));

            Platform.runLater(() -> {
                progressSlider.setValue(newTime);
                String currentTimeStr = Util.formatTime(Duration.seconds(newTime));
                String totalTimeStr = Util.formatTime(totalDuration);
                trackLabel.setText("Now playing: " + audioFiles.get(currentIndex).getName() +
                        " [" + currentTimeStr + "/" + totalTimeStr + "]");
            });

            // задержка сброса флага (100 мс достаточно)
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                Platform.runLater(() -> isSeekingManually = false);
            }).start();
        }
    }

    public void seekBackward() {
        if (mediaPlayer != null) {
            isSeekingManually = true;

            Duration currentTime = mediaPlayer.getCurrentTime();
            double newTime = Math.max(currentTime.toSeconds() - 5, 0);

            mediaPlayer.seek(Duration.seconds(newTime));

            Platform.runLater(() -> {
                progressSlider.setValue(newTime);
                String currentTimeStr = Util.formatTime(Duration.seconds(newTime));
                String totalTimeStr = Util.formatTime(mediaPlayer.getTotalDuration());
                trackLabel.setText("Now playing: " + audioFiles.get(currentIndex).getName() +
                        " [" + currentTimeStr + "/" + totalTimeStr + "]");
            });

            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                Platform.runLater(() -> isSeekingManually = false);
            }).start();
        }
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
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void next() {
        if (audioFiles != null && !audioFiles.isEmpty()) {
            if (isShuffleEnabled) {
                int nextIndex;
                do {
                    nextIndex = random.nextInt(audioFiles.size());
                } while (nextIndex == currentIndex && audioFiles.size() > 1);
                playTrack(nextIndex);
            } else if (currentIndex + 1 < audioFiles.size()) {
                playTrack(++currentIndex);
            }
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
            mediaPlayer.dispose();
        }

        File file = audioFiles.get(index);
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        currentIndex = index;
        trackLabel.setText("Now playing: " + file.getName());
        trackListView.getSelectionModel().select(index);

        mediaPlayer.setOnReady(() -> {
            progressSlider.setMin(0);
            progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            progressSlider.setValue(0);
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!isUserSeeking && !isSeekingManually) {
                Platform.runLater(() -> {
                    progressSlider.setValue(newTime.toSeconds());
                    String currentTime = Util.formatTime(newTime);
                    String totalTime = Util.formatTime(mediaPlayer.getTotalDuration());
                    trackLabel.setText("Now playing: " + file.getName() + " [" + currentTime + "/" + totalTime + "]");
                });
            }
        });

        if (isRepeatEnabled) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } else {
            mediaPlayer.setCycleCount(1);
        }

        mediaPlayer.setOnEndOfMedia(() -> {
            if (!isRepeatEnabled) {
                next();
            }
        });

        mediaPlayer.play();
    }

    public void seek(double seconds) {
        if (mediaPlayer != null) {
            double totalDuration = mediaPlayer.getTotalDuration().toSeconds();
            double newTime = Math.min(Math.max(seconds, 0), totalDuration);
            mediaPlayer.seek(Duration.seconds(newTime));

            Platform.runLater(() -> {
                progressSlider.setValue(newTime);
                String currentTimeStr = Util.formatTime(Duration.seconds(newTime));
                String totalTimeStr = Util.formatTime(mediaPlayer.getTotalDuration());
                trackLabel.setText("Now playing: " + audioFiles.get(currentIndex).getName() +
                        " [" + currentTimeStr + "/" + totalTimeStr + "]");
            });
        }
    }
}
