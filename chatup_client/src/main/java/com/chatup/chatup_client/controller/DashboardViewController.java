package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.Animations;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Component
public class DashboardViewController implements Initializable {

    private final RestClient restClient;

    private final ArrayList<Listener> listeners = new ArrayList<>();

    @FXML
    private HeadbarController headbarController;
    private HeadbarController.Listener headbarListener;

    @FXML
    private SidebarController sidebarController;
    private SidebarController.Listener sidebarListener;

    @FXML
    public Text userNameSurname;

    @FXML
    public Text username;

    @FXML
    public StackPane userAvatar;

    @FXML
    public Button generateAPIKey;

    @FXML
    public TextField apiKey;

    @FXML
    public Button goBack;

    @FXML
    public Button logOutButton;

    @FXML
    public AnchorPane dashboardView;


    @Autowired
    public DashboardViewController(RestClient restClient) {
        this.restClient = restClient;
        this.headbarListener = new HeadbarController.Listener() {
            @Override
            public void onAvatarClicked() {

            }

            @Override
            public void onMessageSearched(Message selectedMessage) {
                for(Listener listener:listeners) listener.onMessageSearched(selectedMessage);
            }
        };

        this.sidebarListener = new SidebarController.Listener() {
            @Override
            public void onAddChannel() {

            }

            @Override
            public void onAddDM() {

            }

            @Override
            public void onChangeChannel(Channel channel) {
                for(Listener listener:listeners){listener.onChangeChannel(channel);}
            }
        };
    }

    public interface Listener{
        void onLogOut(Stage stage);
        void onGoBack();
        void onChangeChannel(Channel channel);
        void onMessageSearched(Message message);
    }
    public void addListener(Listener listener){
        listeners.add(listener);
    }
    public void removeListener(HeadbarController.Listener listener) {listeners.remove(listener);}

    @FXML
    public void onGoBack(){
        for(Listener listener:listeners) listener.onGoBack();
    }

    @FXML
    public void onLogOut() {
        for(Listener listener:listeners) listener.onLogOut((Stage) logOutButton.getScene().getWindow());
    }

    @FXML
    public void onGenerateAPIKey() {
        if (!apiKey.isVisible()) {
            PathTransition apiKeyAnimation = Animations.generateAPIKeyAnimation(generateAPIKey);
            apiKeyAnimation.play();
            apiKey.setVisible(true);
            FadeTransition fadeTransition = Animations.createFadeInTransition(apiKey);
            fadeTransition.play();

            generateAPIKey.setDisable(true);
            generateAPIKey.setText("Regenerate");
        }
    }

    public void subscribeAll(){
        sidebarController.addListener(sidebarListener);
        headbarController.setListener(headbarListener);
    }

    public void unsubscribeAll(){
        sidebarController.removeListener(sidebarListener);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
            UserInfo currentUser = restClient.getCurrentUser();
            userNameSurname.setText(currentUser.toString());
            username.setText(currentUser.getUsername());
            apiKey.setText(restClient.getToken());
            Insets padding = new Insets(0, 0, 0, 0);
            userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 40.0, padding));
            sidebarController.addDM.setVisible(false);
            sidebarController.addChannel.setVisible(false);
    }
}
