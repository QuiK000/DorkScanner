package com.dev.quikkkk.parser.app;

import com.dev.quikkkk.parser.application.ParserService;
import javafx.application.Application;
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
        logArea.setPrefHeight(250);

        VBox root = new VBox(
                chooseDorks,
                dorksField,
                chooseProxy,
                proxyField,
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
        stage.setScene(new Scene(root, 500, 400));
        stage.show();
    }

    private String openFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(stage);
        return file != null ? file.getAbsolutePath() : "";
    }

    private void startParsing() {
        new Thread(() -> {
            try {
                ParserService.run(
                        dorksField.getText(),
                        proxyField.getText(),
                        manualCaptchaCheck.isSelected(),
                        logArea,
                        progressBar
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
