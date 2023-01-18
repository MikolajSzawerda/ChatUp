package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.model.messaging.Message;
import com.chatup.chatup_client.model.users.UserInfo;
import com.chatup.chatup_client.web.RestClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class HeadbarController implements Initializable {

    final Logger logger = LoggerFactory.getLogger(HeadbarController.class);

    private final RestClient restClient;
    private Listener listener;
    @FXML
    private Text userNameSurname;
    @FXML
    private StackPane userAvatar;
    @FXML
    private Button goToDashboard;
    @FXML
    private TextField searchMessageField;
    @FXML
    private ListView<Message> searchMessageResults;

    @Autowired
    public HeadbarController(RestClient restClient) {
        this.restClient = restClient;
        logger.info("HeadBarController created");
    }

    public interface Listener{
        void onAvatarClicked();
        void onMessageSearched(Message selectedMessage);
    }

    void setListener(Listener listener){this.listener=listener;}

    @FXML
    public void onGoToDashboard() {
        listener.onAvatarClicked();
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
        listener.onMessageSearched(selectedMessage);
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
                                    item.getAuthorFirstName() + " " + item.getAuthorLastName(),
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
