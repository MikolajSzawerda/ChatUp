package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.ChangeChatButtonFactory;
import com.chatup.chatup_client.component.ChannelIconFactory;
import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import com.chatup.chatup_client.web.SocketClient;
import javafx.animation.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

@Component
public class DashboardViewController extends ViewController {

    @FXML
    private HeadbarController headbarController;

    @FXML
    private SidebarController sidebarController;

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

    boolean isKeyGenerated;

    @FXML
    public void onGoBack(ActionEvent e) throws IOException{
        application.switchToChatView(e, (Stage) goBack.getScene().getWindow());
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
            generateAPIKey.setText("Regenerate");
        }
    }

    @Override
    public void changeChannel(Channel channel){
        try{
            this.application.switchToChatView(new ActionEvent(), (Stage) username.getScene().getWindow());
        }
        catch (IOException e)
        {
            throw  new UncheckedIOException(e);
        }

    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public DashboardViewController(SocketClient socketClient, RestClient restClient, Application application) {
        super(socketClient, restClient, application);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserInfo currentUser = restClient.getCurrentUser();
        userNameSurname.setText(currentUser.toString());
        username.setText(currentUser.getUsername());
        Insets padding = new Insets(0, 0, 0, 0);
        userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 40.0, padding));
        headbarController.setHeadController(this);
        sidebarController.setHeadController(this);
        Collection<Channel> channels = restClient.listChannels();


        closeDMDialog();
        closeChannelDialog();

        try{
            socketClient.connect();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
