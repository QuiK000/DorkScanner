package com.dev.quikkkk.parser.app;

import com.dev.quikkkk.parser.application.ParserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AppController {
    @FXML
    private HBox headerBox;

    @FXML
    private TextField dorksField;

    @FXML
    private TextField proxyField;

    @FXML
    private TextField limitField;

    @FXML
    private TextField threadsField;

    @FXML
    private CheckBox manualCaptchaCheck;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea logArea;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        makeStageDraggable();
    }

    private void makeStageDraggable() {
        headerBox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        headerBox.setOnMouseDragged(event -> {
            Stage stage = (Stage) headerBox.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    private void onChooseDorks() {
        File file = chooseFile();
        if (file != null) dorksField.setText(file.getAbsolutePath());
    }

    @FXML
    private void onChooseProxy() {
        File file = chooseFile();
        if (file != null) proxyField.setText(file.getAbsolutePath());
    }

    @FXML
    private void onStart() {
        int limit = parseIntOrDefault(limitField.getText(), 50);
        int threads = parseIntOrDefault(threadsField.getText(), 5);

        if (threads < 1) {
            threads = 1;
            threadsField.setText("1");
        }

        final int fLimit = limit;
        final int fThreads = threads;

        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        logArea.appendText("> Инициализация запуска... \n");

        new Thread(() -> {
            try {
                ParserService.run(
                        dorksField.getText(),
                        proxyField.getText(),
                        manualCaptchaCheck.isSelected(),
                        fLimit,
                        fThreads,
                        logArea,
                        progressBar,
                        statusLabel
                );

                Platform.runLater(() -> {
                    progressBar.setProgress(1.0);
                    logArea.appendText("> Выполнено успешно.\n");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    logArea.appendText("> Ошибка: " + e.getMessage() + "\n");
                    statusLabel.setText("Статус: Ошибка");
                });
            }
        }).start();
    }

    @FXML
    private void onStop() {
        ParserService.stop();
        statusLabel.setText("Статус: Остановка...");
    }

    @FXML
    private void onMinimize() {
        Stage stage = (Stage) headerBox.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void onClose() {
        ParserService.stop();
        Platform.exit();
    }

    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(headerBox.getScene().getWindow());
    }

    private int parseIntOrDefault(String value, int def) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
