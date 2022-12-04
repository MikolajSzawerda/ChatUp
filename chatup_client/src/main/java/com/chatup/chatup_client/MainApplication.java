package com.chatup.chatup_client;

import com.chatup.chatup_client.web.AuthClient;
import com.chatup.chatup_client.web.RestClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root  = FXMLLoader.load(getClass().getResource("/login-view.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Roboto+Slab");
        stage.setTitle("ChatUp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}