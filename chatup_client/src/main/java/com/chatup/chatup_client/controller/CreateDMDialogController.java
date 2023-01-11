package com.chatup.chatup_client.controller;

import com.chatup.chatup_client.component.AvatarFactory;
import com.chatup.chatup_client.component.skin.MyButtonSkin2;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.web.RestClient;
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
import java.util.ResourceBundle;
@Component
public class CreateDMDialogController implements Initializable {

    private ViewController headController;
    private final RestClient restClient;
    @FXML
    public Pane addDMDialog;
    @FXML
    public Button closeDMDialogButton;

    @FXML
    public ListView<UserInfo> searchUserResultsDM;

    @FXML
    public TextField searchFieldDM;

    @Autowired
    public CreateDMDialogController(RestClient restClient) {
        this.restClient = restClient;
    }

    @FXML
    public void createDM(){
        UserInfo selectedUser = searchUserResultsDM.getSelectionModel().getSelectedItem();
        headController.createDM(selectedUser.getId());
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
            UserInfo currentUser = restClient.getCurrentUser();
            searchUserResultsDM.setVisible(true);
            Collection<UserInfo> searchResultsCollection = restClient.searchUsers(searchFieldDM.getText());
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

    public void setHeadController(ViewController headController){
        this.headController=headController;
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
