package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.MainApplication;
import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class HeadbarController implements Initializable {

    final Logger logger = LoggerFactory.getLogger(HeadbarController.class);

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
    public TextField searchMessageField;

    @FXML
    public ListView<Message> searchMessageResults;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public HeadbarController(RestClient restClient, Application application) {
        this.restClient = restClient;
        this.application = (MainApplication) application;
        logger.info("HeadBarController created");
    }

    @FXML
    public void onGoToDashboard(ActionEvent e) throws IOException {
        application.switchToDashboardView(e, (Stage) goToDashboard.getScene().getWindow());
    }

    public void setHeadController(ViewController headController){
        this.headController=headController;
    }

    @FXML
    public void onSearchMessage(){
        if(searchMessageField.getText().length() == 0) {
            searchMessageResults.setVisible(false);
        } else {
            searchMessageResults.setVisible(true);
            List<Message> matchingMessages = new ArrayList<>(restClient.searchMessages(searchMessageField.getText()));

            ObservableList<Message> searchResultsList = FXCollections.observableArrayList(matchingMessages);
            searchMessageResults.setItems(searchResultsList);
        }

    }

    @FXML
    public void scrollToMessage(){
        Message selectedMessage = searchMessageResults.getSelectionModel().getSelectedItem();
        searchMessageResults.setVisible(false);
        searchMessageField.setText("");
        this.headController.scrollToMessage(selectedMessage);
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources){
        searchMessageResults.setVisible(false);

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
                    Insets padding = new Insets(0, 5, 0, 0);
                    StackPane avatar = AvatarFactory.createAvatar(
                            item.getAuthorFirstName()+" "+item.getAuthorLastName(),
                            13.0,
                            padding
                    );

                    setText(item.getContent());
                    setGraphic(avatar);
                }
            }
        });
    }
}
