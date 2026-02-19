package com.dev.quikkkk.parser.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @Override
    public void start(Stage stage) {
        Button chooseDorks = new Button("dorks.txt: ");
        chooseDorks.setOnAction(_ -> dorksField.setText(openFile(stage)));

        Button chooseProxy = new Button("proxy.txt: ");
        chooseProxy.setOnAction(_ -> proxyField.setText(openFile(stage)));

        Button startBtn = new Button("Старт");

        logArea.setEditable(false);
        logArea.setPrefHeight(250);

        VBox root = new VBox(
                chooseDorks,
                dorksField,
                chooseProxy,
                proxyField,
                startBtn,
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
}
