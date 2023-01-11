package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.MessageFactory;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ResourceBundle;

@Component
public class HeadbarController implements Initializable {


    private final ChatViewController headController;

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

    @Autowired
    public HeadbarController(ChatViewController chatViewController) {
        this.headController = chatViewController;
        headController.logger.info("HeadbarController created");
    }

    @FXML
    public void onGoToDashboard() {
        headController.switchToDashboard();
    }

    @FXML
    public void searchMessages(){
        //TODO implement adding search results to observable list and bind it to searchMessagesResult List View
    }

    @FXML
    public void goToFoundMessage(){
       headController.goToMessage(searchMessageResults.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources){
        UserInfo currentUser = headController.getRestClient().getCurrentUser();
        headController.logger.info("Logged in user: {}", currentUser.toString());
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
