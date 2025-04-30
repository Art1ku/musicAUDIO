package music.demo.util;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static File chooseDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose directory with music");
        return chooser.showDialog(new Stage());
    }

    public static List<File> getAudioFiles(File directory) {
        return Arrays.stream(directory.listFiles())
                .filter(f -> f.getName().endsWith(".mp3") || f.getName().endsWith(".wav"))
                .collect(Collectors.toList());
    }

    public static String getFileName(String path) {
        return new File(path).getName();
    }

    public static String formatTime(Duration duration) {
        if (duration == null) {
            return "00:00";
        }
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
