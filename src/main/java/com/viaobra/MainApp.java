package com.viaobra;

import com.viaobra.db.Schema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Schema.init();

        FXMLLoader fxml = new FXMLLoader(MainApp.class.getResource("/com/viaobra/main.fxml"));
        Parent root = fxml.load();

        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(
                MainApp.class.getResource("/com/viaobra/styles.css").toExternalForm()
        );

        stage.setTitle("VIAOBRA – Viabilidade de Obras");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
