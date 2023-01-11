package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

@Component
public class DashboardController implements Initializable {

    @FXML
    public Text userNameSurname;

    @FXML
    public Text username;

    @FXML
    public StackPane userAvatar;

    @FXML
    public Button generateAPIKey;

    @FXML
    public Text apiKey;

    @FXML
    public ListView<String> direct;

    @FXML
    public ListView<String> channels;

    @FXML
    public Button goBack;

    @FXML
    public Button logOutButton;

    @FXML
    public AnchorPane dashboard;

    boolean isKeyGenerated;

    private ChatViewController headController;

    @FXML
    public void onGoBack(ActionEvent e) throws IOException{
        headController.switchToMessaging();
    }

    @FXML
    public void onGenerateAPIKey(){
        if(!isKeyGenerated) {
            Path path = new Path();
            MoveTo start = new MoveTo(60.0, 12.0);
            path.getElements().add(start);
            path.getElements().add(new HLineTo(300));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(200));
            pathTransition.setPath(path);
            pathTransition.setNode(generateAPIKey);
            pathTransition.setCycleCount((int) 1f);
            pathTransition.setAutoReverse(false);
            pathTransition.play();

            apiKey.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(200), apiKey);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

            isKeyGenerated = true;
            generateAPIKey.setDisable(true);
        }
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public DashboardController(ChatViewController chatViewController) {
        this.headController = chatViewController;
    }



    public void onLogOut(ActionEvent e) throws IOException{
        headController.switchToLoginView();
        //TODO on log out
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserInfo currentUser = headController.getRestClient().getCurrentUser();
        userNameSurname.setText(currentUser.toString());
        username.setText(currentUser.getUsername());

        Insets padding = new Insets(0, 0, 0, 0);
        userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 40.0, padding));

//        closeDMDialog();
//        closeChannelDialog();
//        sidebarController.direct.refresh();
//        sidebarController.channels.refresh();

        logOutButton.setOnAction(event->{
            try {
                onLogOut(event);
            }
            catch (IOException e){
                throw new UncheckedIOException(e);
            }
        });
        logOutButton.setSkin(new MyButtonSkin2(logOutButton));

    }

}
