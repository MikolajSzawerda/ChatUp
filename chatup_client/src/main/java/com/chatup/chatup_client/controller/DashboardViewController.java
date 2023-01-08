package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.ChangeChatButtonFactory;
import com.chatup.chatup_client.component.ChannelIconFactory;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
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
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DashboardViewController implements Initializable {


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

    private final RestClient restClient;
    private final MainApplication application;

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

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public DashboardViewController(RestClient restClient, Application application) {
        this.restClient = restClient;
        this.application = (MainApplication) application;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserInfo currentUser = restClient.getCurrentUser();
        userNameSurname.setText(currentUser.toString());
        username.setText(currentUser.getUsername());
        Insets padding = new Insets(0, 0, 0, 0);
        userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 40.0, padding));


        direct.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {

                    Insets padding = new Insets(0, 5, 0, 0);
                    StackPane avatar = AvatarFactory.createAvatar(item, 18.0, padding);
                    Button directMessageButton = ChangeChatButtonFactory.createChangeChatButton(avatar, item, param.getWidth());

                    setGraphic(directMessageButton);

                }
            }
        });

        channels.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {
                    Node channelIcon;
                    if (item.equals("Kanał drugi")) {
                        channelIcon = ChannelIconFactory.createChannelIcon(true, 12);
                    } else {
                        channelIcon = ChannelIconFactory.createChannelIcon(false, 12);
                    }

                    Button channelButton = ChangeChatButtonFactory.createChangeChatButton(channelIcon, item, param.getWidth());
                    channelButton.getStyleClass().add("my-button");

                    final Animation animation = new Transition() {
                        {
                            setCycleDuration(Duration.millis(400));
                            setInterpolator(Interpolator.EASE_OUT);
                        }
                        @Override
                        protected void interpolate(double frac) {
                            Color vColor = new Color(0.33, 0.42, 0.86, 1 - frac);
                            channelButton.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
                        }

                    };
                    channelButton.setBackground(new Background(new BackgroundFill(new Color(0, 0 ,0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
                    animation.setOnFinished(event -> goBack.fire());
                    channelButton.setOnAction(e -> animation.play());

                    setGraphic(channelButton);
                }
            }
        });

        ObservableList<String> channelList = FXCollections.observableArrayList();
        channelList.add("Kanał pierwszy");
        channelList.add("Kanał drugi");
        channels.setItems(channelList);

        ObservableList<String> directMessages = FXCollections.observableArrayList();
        directMessages.add("Dawid Kaszyński");
        directMessages.add("Jan Kowalczewski");
        directMessages.add("Mikołaj Szawerda");
        direct.setItems(directMessages);
    }
}
