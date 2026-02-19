package com.dev.quikkkk.parser.app;

import com.dev.quikkkk.parser.application.ParserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 790, 560);

        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("DORK СКАНЕР");

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        ParserService.stop();
    }
}