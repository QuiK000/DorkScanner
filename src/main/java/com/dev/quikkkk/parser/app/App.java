package com.dev.quikkkk.parser.app;

import com.dev.quikkkk.parser.application.ParserService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {
    private final TextField dorksField = new TextField();
    private final TextField proxyField = new TextField();
    private final TextField limitField = new TextField("50");
    private final TextField threadsField = new TextField("5");
    private final TextArea logArea = new TextArea();
    private final ProgressBar progressBar = new ProgressBar();
    private final CheckBox manualCaptchaCheck = new CheckBox("Manual Captcha");

    @Override
    public void start(Stage stage) {
        Button chooseDorks = new Button("dorks.txt: ");
        chooseDorks.setOnAction(_ -> dorksField.setText(openFile(stage)));

        Button chooseProxy = new Button("proxy.txt: ");
        chooseProxy.setOnAction(_ -> proxyField.setText(openFile(stage)));

        Button startBtn = new Button("Start");
        startBtn.setOnAction(_ -> startParsing());

        Button stopBtn = new Button("Stop");
        stopBtn.setOnAction(_ -> ParserService.stop());

        logArea.setEditable(false);
        logArea.setPrefHeight(200);
        progressBar.setPrefWidth(Double.MAX_VALUE);

        VBox root = new VBox(
                chooseDorks,
                dorksField,
                chooseProxy,
                proxyField,
                new Label("Results Limit per Dork: "),
                limitField,
                new Label("Threads (Cores): "),
                threadsField,
                manualCaptchaCheck,
                startBtn,
                stopBtn,
                new Label("Progress: "),
                progressBar,
                new Label("Log:"),
                logArea
        );

        root.setPadding(new Insets(15));
        stage.setTitle("Dork Parser");
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    private String openFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(stage);
        return file != null ? file.getAbsolutePath() : "";
    }

    private void startParsing() {
        int limit;
        try {
            limit = Integer.parseInt(limitField.getText().trim());
        } catch (NumberFormatException e) {
            limit = 50;
            limitField.setText("50");
        }

        int threads;
        try {
            threads = Integer.parseInt(threadsField.getText().trim());
            if (threads < 1) threads = 1;
        } catch (NumberFormatException e) {
            threads = 5;
            threadsField.setText("5");
        }

        final int finalLimit = limit;
        final int finalThreads = threads;

        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        new Thread(() -> {
            try {
                ParserService.run(
                        dorksField.getText(),
                        proxyField.getText(),
                        manualCaptchaCheck.isSelected(),
                        finalLimit,
                        finalThreads,
                        logArea,
                        progressBar
                );
                Platform.runLater(() -> progressBar.setProgress(1.0));
            } catch (Exception e) {
                Platform.runLater(() -> progressBar.setProgress(0));
                throw new RuntimeException(e);
            }
        }).start();
    }
}
