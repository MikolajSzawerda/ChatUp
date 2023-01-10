package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.MessageFactory;
import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.result.view.View;

import java.io.IOException;
import java.util.ResourceBundle;

@Component
public class HeadbarController implements Initializable {

    final Logger logger = LoggerFactory.getLogger(ChatViewController.class);

    private ViewController headController;
    private final RestClient restClient;
    private final MainApplication application;

    @FXML
    public Text userNameSurname;

    @FXML
    public StackPane userAvatar;

    @FXML
    public Button goToDashboard;

    @FXML
    public ListView<Message> searchMessageResults;

    @FXML
    public TextField searchMessage;

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

    @FXML
    public void searchMessages(){
        //TODO implement adding search results to observable list and bind it to searchMessagesResult List View
    }

    @FXML
    public void goToFoundMessage(){
       headController.goToMessage(searchMessageResults.getSelectionModel().getSelectedItem());
    }

    public void setHeadController(ViewController headController){
        this.headController=headController;
    }
    @Override
    public void initialize(java.net.URL location, ResourceBundle resources){
        UserInfo currentUser = restClient.getCurrentUser();
        logger.info("Logged in user: {}", currentUser.toString());
        userNameSurname.setText(currentUser.toString());
        Insets padding = new Insets(0, 0, 0, 0);
        userAvatar.getChildren().addAll(AvatarFactory.createAvatar(currentUser.toString(), 25.0, padding));
        searchMessageResults.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {
                    setMaxWidth(param.getWidth());
                    setMinWidth(param.getWidth() - 100);
                    setPrefWidth(param.getWidth() - 100);

                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    GridPane message = MessageFactory.createMessage(item.getContent(),
                            item.getAuthorFirstName() + " " + item.getAuthorLastName(), item.getAuthorUsername(), param.getWidth());

                    setGraphic(message);

                }
            }
        });
    }


}
