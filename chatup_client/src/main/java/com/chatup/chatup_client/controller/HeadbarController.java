package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ResourceBundle;

@Component
public class HeadbarController implements Initializable {

    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);

    private ChatViewController headController;
    private final RestClient restClient;
    private final MainApplication application;

    @FXML
    public Text userNameSurname;

    @FXML
    public StackPane userAvatar;

    @FXML
    public Button goToDashboard;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public HeadbarController(RestClient restClient, Application application) {
        this.restClient = restClient;
        this.application = (MainApplication) application;
        logger.info("ChatViewController created");
    }

    @FXML
    public void onGoToDashboard(ActionEvent e) throws IOException {
        application.switchToDashboardView(e, (Stage) goToDashboard.getScene().getWindow());
    }

    public void setHeadController(ChatViewController headController){
        this.headController=headController;
    }
    @Override
    public void initialize(java.net.URL location, ResourceBundle resources){
        UserInfo currentUser = restClient.getCurrentUser();
        logger.info("Logged in user: {}", currentUser.toString());
        userNameSurname.setText(currentUser.toString());
        Insets padding = new Insets(0, 0, 0, 0);
        userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 25.0, padding));
    }


}
