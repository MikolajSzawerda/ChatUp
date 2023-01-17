package com.chatup.chatup_client;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;

public class MainApplication extends Application {
    ConfigurableApplicationContext context;

    Stage stage;

    @Override
    public void init() {
        ApplicationContextInitializer<GenericApplicationContext> initializer = ac -> {
            ac.registerBean(Application.class, () -> MainApplication.this);
            ac.registerBean(Parameters.class, this::getParameters);
            ac.registerBean(HostServices.class, this::getHostServices);
        };
        context = new SpringApplicationBuilder().sources(ClientSpringApplication.class)
                .initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        loader.setControllerFactory(context::getBean);
        Parent root  = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("https://fonts.googleapis.com/css?family=Roboto+Slab");
        stage.setTitle("ChatUp");
        stage.getIcons().add(new Image("img/icon.png"));
        stage.setScene(scene);
        stage.show();
        this.stage = (Stage) scene.getWindow();
    }

    @Override
    public void stop(){
        context.close();
        Platform.exit();
    }

    public void switchToChatView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatup-view-new.fxml"));
        loadView(loader);
    }

    public void switchToLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        loadView(loader);
    }

    public void switchToDashboardView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard-view.fxml"));
        loadView(loader);
    }

    private void loadView(FXMLLoader loader) throws IOException {
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("https://fonts.googleapis.com/css?family=Roboto+Slab");
        stage.setScene(scene);
        stage.show();
    }



}
