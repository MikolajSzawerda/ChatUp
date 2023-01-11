package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.model.UserInfo;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
@Component
public class CreateDMDialogController implements Initializable {

    private final ChatViewController headController;
    @FXML
    public Pane addDMDialog;
    @FXML
    public Button closeDMDialogButton;

    @FXML
    public ListView<UserInfo> searchUserResultsDM;

    @FXML
    public TextField searchFieldDM;

    @Autowired
    public CreateDMDialogController(ChatViewController chatViewController) {
        this.headController = chatViewController;
        headController.logger.info("DMDialogController created");
    }

    @FXML
    public void createDM(){
        UserInfo currentUser = headController.getRestClient().getCurrentUser();
        UserInfo selectedUser = searchUserResultsDM.getSelectionModel().getSelectedItem();
        HashSet<Long> userIds = new HashSet<>();
        userIds.add(selectedUser.getId());
        userIds.add(currentUser.getId());
        //TODO check if DM channel with given person does not exist
        headController.getRestClient().createChannel(selectedUser.getFirstName()+" "+selectedUser.getLastName(), true,  true, userIds);
        headController.closeDMDialog();
    }


    @FXML
    public void onCloseDMDialogButton(){
        FadeTransition ft = new FadeTransition(Duration.millis(200), addDMDialog);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        headController.closeDMDialog();
    }


    @FXML
    public void onSearchUserDM(){
        if(searchFieldDM.getText().length() == 0) searchUserResultsDM.setVisible(false);
        else {
            UserInfo currentUser = headController.getRestClient().getCurrentUser();
            searchUserResultsDM.setVisible(true);
            Collection<UserInfo> searchResultsCollection = headController.getRestClient().searchUsers(searchFieldDM.getText());
            searchResultsCollection.remove(currentUser);
            ObservableList<UserInfo> searchResultsList = FXCollections.observableArrayList(searchResultsCollection);
            searchUserResultsDM.setItems(searchResultsList);
            searchUserResultsDM.prefHeightProperty().bind(Bindings.size((searchResultsList)).multiply(33));
        }
    }

    public void show(){
        addDMDialog.setVisible(true);
        searchFieldDM.setText("");
        searchUserResultsDM.setVisible(false);
        FadeTransition ft = new FadeTransition(Duration.millis(200), addDMDialog);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    public void close(){
        addDMDialog.setVisible(false);

        FadeTransition ft = new FadeTransition(Duration.millis(200), addDMDialog);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

    @Override
    public void initialize(java.net.URL location, ResourceBundle resources) {
        addDMDialog.setVisible(false);
        closeDMDialogButton.setOnAction(e->onCloseDMDialogButton());
        closeDMDialogButton.setSkin(new MyButtonSkin2(closeDMDialogButton));
        searchUserResultsDM.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UserInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item != null) {

                    Insets padding = new Insets(0, 5, 0, 0);
                    StackPane avatar = AvatarFactory.createAvatar(item.getFirstName()+" "+item.getLastName(), 13.0, padding);
                    setText(item.getFirstName()+" "+item.getLastName());
                    setGraphic(avatar);


                }
            }
        });
//
    }
}
